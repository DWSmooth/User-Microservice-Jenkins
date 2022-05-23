package com.smoothstack.usermicroservice.service;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.smoothstack.common.models.*;
import com.smoothstack.common.repositories.*;

import com.smoothstack.usermicroservice.data.jwt.ConfirmEmailToken;
import com.smoothstack.usermicroservice.data.jwt.ResetPasswordToken;
import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import com.smoothstack.usermicroservice.exceptions.*;
import com.smoothstack.usermicroservice.service.messaging.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.NotSupportedException;

@Service
public class EmailConfirmationService {
    private static final Integer confirmEmailExpiryHours = 24 * 7;
    private static final Integer resetPasswordExpiryHours = 24 * 1;
    private static final String confirmEmailPath = "/ufd/user-service/confirmEmail";
    private static final String resetPasswordPath = "/ufd/user-service/resetPassword";

    private String url;

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserInformationRepository userInfoRepo;

    @Autowired
    MessageTypeRepository msgTypeRepo;

    @Autowired
    MessageRepository msgRepo;

    @Autowired
    SecureRandomService randomService;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordReqsService pwService;

    @Autowired
    MessagingService msgService;

    @Autowired
    public EmailConfirmationService(ConfigService config) {
        this.url = config.getUrlAddress();
    }

    public void sendConfirmEmail(SendConfirmEmailBody body)
            throws UserNotFoundException, SendMsgFailureException {
        Optional<User> userResult = userRepo.findById(body.getUserId());
        if (userResult.isEmpty())
            throw new UserNotFoundException();

        User user = userResult.get();

        try {
            // Grab user information
            UserInformation userInfo = user.getUserInformation();
            CommunicationMethod cm = userInfo.getCommunicationType();
            String method = cm.getName();

            // Prepare message
            String jwt = createConfirmEmailJwt(user.getId());
            String link = confirmEmailLink(jwt);

            switch (method) {
                case "sms":
                {
                    String phone = userInfo.getPhoneNumber();

                    // TODO: Write a better SMS body
                    msgService.sendSMS(phone, link);
                    break;
                }
                case "email":
                {
                    String email = userInfo.getEmail();
                    String subject = "MEGA BYTES: Confirm Email Link";

                    // TODO: Write an actual HTML body
                    msgService.sendEmail(email, subject, link);
                    break;
                }
                default:
                    throw new NotSupportedException("Cannot use \"" + method + "\" as a communication type");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SendMsgFailureException();
        }
    }

    public void sendResetPassword(SendResetPasswordBody body) throws SendMsgFailureException {
        Optional<UserInformation> userInfoResult = userInfoRepo.findTopByEmail(body.getEmail());

        // Must return 200 OK as security measure.
        if (userInfoResult.isEmpty())
            return;

        UserInformation userInfo = userInfoResult.get();

        try {
            // Grab user information
            User user = userInfo.getUser();
            Integer userId = user.getId();
            CommunicationMethod cm = userInfo.getCommunicationType();
            String method = cm.getName();

            // Prepare message
            MessageType msgType = getMessageType("forgot-password");
            String confirmation = randomService.generateAlphanumericString(32);
            String jwt = createResetPasswordJwt(userId, confirmation);
            String link = resetPasswordLink(jwt);
            Message msg = createMsg(msgType, cm, confirmation);

            switch (method) {
                case "sms":
                {
                    String phone = userInfo.getPhoneNumber();
                    msgRepo.save(msg);

                    // TODO: Write a better SMS body
                    msgService.sendSMS(phone, link);
                    break;
                }
                case "email":
                {
                    String email = userInfo.getEmail();
                    String subject = "MEGA BYTES: Reset Password Link";
                    msgRepo.save(msg);

                    // TODO: Write an actual HTML body
                    msgService.sendEmail(email, subject, link);
                    break;
                }
                default:
                    throw new NotSupportedException("Cannot use \"" + method + "\" as a communication type");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SendMsgFailureException();
        }
    }

    public void confirmEmail(String token) throws UserNotFoundException, TokenInvalidException {
        // Decode JWT
        ConfirmEmailToken jwt;
        try {
            jwt = jwtService.validateConfirmEmailToken(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException();
        }

        // Check if user exists
        Optional<User> userResult = userRepo.findById(jwt.getUserId());

        if (userResult.isEmpty())
            throw new UserNotFoundException();

        User user = userResult.get();

        // Commit email confirmed to database
        UserInformation userInfo = user.getUserInformation();
        userInfo.setEmailConfirmed(true);
        userInfoRepo.save(userInfo);

        // Send 200 OK
    }

    public void resetPassword(String token, ResetPasswordBody body) throws MsgInvalidException, InsufficientPasswordException, TokenInvalidException {
        // Decode JWT
        ResetPasswordToken jwt;
        try {
            jwt = jwtService.validateResetPasswordToken(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException();
        }

        // Validate new password
        String password = body.getPassword();
        if (!pwService.verifyPassword(password))
            throw new InsufficientPasswordException();

        // Check if user exists
        Optional<User> userResult = userRepo.findById(jwt.getUserId());

        // Throws token invalid as userId is not a user input
        if (userResult.isEmpty())
            throw new TokenInvalidException();

        User user = userResult.get();

        Optional<Message> msgResult = msgRepo.findTopByConfirmationCode(jwt.getConfirmation());

        // Check if msg with confirmation code exists
        if (msgResult.isEmpty())
            throw new MsgInvalidException();

        Message msg = msgResult.get();

        // Check if msg is already inactive
        if (!msg.getIsActive())
            throw new MsgInvalidException();

        // Commit new password to database
        user.setPassword(password);
        userRepo.save(user);

        // Commit deactivated msg to database
        msg.setIsActive(false);
        msgRepo.save(msg);
    }

    // Private Implementation

    // This link is served to the user and routes to a confirm email action.
    private String confirmEmailLink(String token) {
        return this.url + confirmEmailPath + "?token=" + token;
    }

    // This link is served to the user and routes to a reset password action.
    private String resetPasswordLink(String token) {
        return this.url + resetPasswordPath + "?token=" + token;
    }

    private String createConfirmEmailJwt(Integer userId) {
        Date expiry = addHoursToDate(new Date(), confirmEmailExpiryHours);
        ConfirmEmailToken token = new ConfirmEmailToken(userId, expiry);
        return jwtService.generateToken(token);
    }

    private String createResetPasswordJwt(Integer userId, String confirmation) {
        Date expiry = addHoursToDate(new Date(), resetPasswordExpiryHours);
        ResetPasswordToken token = new ResetPasswordToken(userId, confirmation, expiry);
        return jwtService.generateToken(token);
    }

    private Message createMsg(MessageType type, CommunicationMethod method, String confirmation) {
        Message msg = new Message();
        msg.setType(type);
        msg.setCommunicationType(method);
        msg.setConfirmationCode(confirmation);
        msg.setIsActive(true);
        msg.setTimeSent(Instant.now());
        return msg;
    }

    // TODO: Should this be repository code?
    private MessageType getMessageType(String name) {
        Optional<MessageType> result = msgTypeRepo.findTopByName(name);

        if (result.isPresent())
            return result.get();

        // Create the message type
        MessageType msgType = new MessageType();
        msgType.setName(name);

        // Needs to flush to get the correct SQL generated ID.
        msgTypeRepo.saveAndFlush(msgType);
        return msgType;
    }

    private static Date addHoursToDate(Date date, Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
