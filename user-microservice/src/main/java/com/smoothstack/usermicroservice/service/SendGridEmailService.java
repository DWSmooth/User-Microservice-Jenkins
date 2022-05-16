package com.smoothstack.usermicroservice.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService {

    private SendGrid sendGrid;

    private Email fromAddress;

    public SendGridEmailService() {
        this.sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        this.fromAddress = new Email(System.getenv("SENDGRID_EMAIL"));
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
