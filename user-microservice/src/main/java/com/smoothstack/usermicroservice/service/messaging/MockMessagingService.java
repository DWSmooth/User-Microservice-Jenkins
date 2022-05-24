package com.smoothstack.usermicroservice.service.messaging;

public class MockMessagingService implements MessagingService {
    public void sendEmail(String email, String subject, String htmlBody) {
        String output = String.format("MockEmail{ email: %s, subject: %s, htmlBody: %s", email, subject, htmlBody);
        System.out.println(output);
    }

    public void sendSMS(String phone, String message) {
        String output = String.format("MockSms{ phone: %s, message: %s }", phone, message);
        System.out.println(output);
    }
}
