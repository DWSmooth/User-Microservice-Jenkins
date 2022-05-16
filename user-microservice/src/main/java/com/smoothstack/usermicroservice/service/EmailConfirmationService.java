package com.smoothstack.usermicroservice.service;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.smoothstack.common.models.*;
import com.smoothstack.common.repositories.*;

import com.smoothstack.usermicroservice.data.jwt.ConfirmEmailToken;
import com.smoothstack.usermicroservice.data.jwt.ResetPasswordToken;
import com.smoothstack.usermicroservice.data.rest.ResetPasswordBody;
import com.smoothstack.usermicroservice.data.rest.SendConfirmEmailBody;
import com.smoothstack.usermicroservice.data.rest.SendResetPasswordBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmationService {
    private static Integer confirmEmailExpiryHours = 24 * 7;
    private static Integer resetPasswordExpiryHours = 24 * 1;

    @Autowired
    UserRepository userRepo;

    @Autowired
    UserInformationRepository userInfoRepo;

    @Autowired
    MessageTypeRepository msgTypeRepo;

    @Autowired
    MessageRepository msgRepo;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordReqsService pwService;

    @Autowired
    SendGridEmailService emailService;

    public void sendConfirmEmail(SendConfirmEmailBody body) {
        User user = userRepo.getById(body.getUserId());

        if (user == null) {
            // TODO: Bad request exception
            return;
        }

        try {
            // Grab user information
            Integer userId = user.getId();
            UserInformation userInfo = user.getUserInformation();
            String email = userInfo.getEmail();

            // Generate JWT
            ConfirmEmailToken token = new ConfirmEmailToken();
            token.setUserId(userId);
            token.setExpiry(addHoursToDate(new Date(), confirmEmailExpiryHours));
            String jwt = jwtService.generateToken(token);

            // Generate/Send email
            String link = "http://localhost:8080/"
                    + "ufd/user-service/confirmEmail"
                    + "?token="
                    + jwt;
            String subject = "MEGA BYTES: Confirm Email Link";
            emailService.sendTextPlain(email, subject, link);
        } catch (Exception e) {
            //TODO: 500 Server Error
            return;
        }
    }

    public void sendResetPassword(SendResetPasswordBody body) {
        UserInformation userInfo = userInfoRepo.getByEmail(body.getEmail());

        if (userInfo == null) {
            // TODO: 200 OK even though nothing happened
            // for security purposes
            return;
        }

        try {
            // Grab user information
            User user = userInfo.getUser();
            Integer userId = user.getId();
            List<CommunicationMethod> commsMethods = user.getCommunicationMethods();

            if (commsMethods.isEmpty()) {
                //TODO: actually throw an error here
                System.err.println("A User has no Communication Methods!");
                return;
            }

            // TODO: Rewrite once communication methods are improved.
            CommunicationMethod commsMethod = commsMethods.get(0);
            String email = userInfo.getEmail();

            // Grab message type
            // TODO: Rewrite once msgTypes are easier to grab.
            MessageType msgType = msgTypeRepo.getById(1);

            // Generate confirmation code
            // TODO: Write an actually random string sequence
            String confirmation = "TEST_CONFIRMATION_" + Math.random();

            // Generate message object
            Message msg = new Message();
            msg.setType(msgType);
            msg.setCommunicationType(commsMethod);
            msg.setIsActive(true);
            msg.setTimeSent(Instant.now());
            msg.setConfirmationCode(confirmation);

            // Commit message to database
            msgRepo.save(msg);

            // Generate JWT
            ResetPasswordToken token = new ResetPasswordToken();
            token.setUserId(userId);
            token.setConfirmation(confirmation);
            token.setExpiry(addHoursToDate(new Date(), resetPasswordExpiryHours));
            String jwt = jwtService.generateToken(token);

            // Generate/Send email
            String link = "http://localhost:8080/"
                    + "ufd/user-service/resetPassword"
                    + "?token="
                    + jwt;
            String subject = "MEGA BYTES: Reset Password Link";
            emailService.sendTextPlain(email, subject, link);

        } catch (Exception e) {
            //TODO: 500 Server Error
            e.printStackTrace();
            return;
        }
    }

    public void confirmEmail(String token) {
        // Decode JWT
        ConfirmEmailToken jwt;
        try {
            jwt = jwtService.validateConfirmEmailToken(token);
        } catch (JWTVerificationException e) {
            // TODO: Bad request exception
            return;
        }

        // Check if user exists
        User user = userRepo.getById(jwt.getUserId());

        if (user == null) {
            // TODO: Bad request exception
            return;
        }

        // Commit email confirmed to database
        UserInformation userInfo = user.getUserInformation();
        userInfo.setEmailConfirmed(true);
        userInfoRepo.save(userInfo);

        // Send 200 OK
    }

    public void resetPassword(String token, ResetPasswordBody body) {
        // Decode JWT
        ResetPasswordToken jwt;
        try {
            jwt = jwtService.validateResetPasswordToken(token);
        } catch (JWTVerificationException e) {
            // TODO: Bad request exception
            return;
        }

        // Check if user exists
        User user = userRepo.getById(jwt.getUserId());

        if (user == null) {
            // TODO: Bad request exception
            return;
        }

        // Check if confirmation code exists
        Message msg = msgRepo.getByConfirmationCode(jwt.getConfirmation());

        if (msg == null) {
            // TODO: Bad request exception
            return;
        }

        // Check if msg is not already deactivated
        if (!msg.getIsActive()) {
            // TODO: Bad request exception
            return;
        }

        // Validate new password
        String password = body.getPassword();
        if (!pwService.verifyPassword(password)) {
            // TODO: Bad request exception
            return;
        }

        // Commit new password to database
        user.setPassword(password);
        userRepo.save(user);

        // Commit deactivated msg to database
        msg.setIsActive(false);
        msgRepo.save(msg);

        // Send 200 OK
    }

    private static Date addHoursToDate(Date date, Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
