package com.smoothstack.usermicroservice.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService {

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
}
