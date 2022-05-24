package com.smoothstack.usermicroservice.service.messaging;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.smoothstack.usermicroservice.exceptions.SendMsgFailureException;
import com.smoothstack.usermicroservice.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SendGridEmailService implements MessagingService {

    private SendGrid sendGrid;

    private Email fromAddress;

    @Autowired
    public SendGridEmailService(ConfigService config) {
        this.sendGrid = new SendGrid(config.getSendGridApiKey());
        this.fromAddress = new Email(config.getSendGridEmail());
    }

    public Response sendMail(String email, String subject, Content content) throws IOException {
        Mail mail = new Mail(this.fromAddress, subject, new Email(email), content);

        Request req = new Request();
        req.setMethod(Method.POST);
        req.setEndpoint("mail/send");
        req.setBody(mail.build());
        return sendGrid.api(req);
    }

    public Response sendTextPlain(String email, String subject, String body) throws IOException {
        Content content = new Content("text/plain", body);
        return sendMail(email, subject, content);
    }

    public void sendEmail(String email, String subject, String htmlBody) throws SendMsgFailureException {
        try {
            sendTextPlain(email, subject, htmlBody);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SendMsgFailureException();
        }
    }

    public void sendSMS(String phone, String message) throws SendMsgFailureException {
        throw new SendMsgFailureException("SendGrid service does not support SMS");
    }
}
