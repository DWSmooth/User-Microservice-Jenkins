package com.smoothstack.usermicroservice.service;

import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    public static final String urlAddress = "URL_ADDRESS";
    public static final String urlAddressDefault = "http://localhost:8080";

    public static final String jwtSecret = "JWT_SECRET";
    public static final String jwtSecretDefault = "testSecret123";

    public static final String sendGridApiKey = "SENDGRID_API_KEY";
    public static final String sendGridEmail = "SENDGRID_EMAIL";

    public static final String awsAccessKeyId = "AWS_ACCESS_KEY_ID";
    public static final String awsSecretAccessKey = "AWS_SECRET_ACCESS_KEY";
    public static final String awsPinpointAppId = "AWS_PINPOINT_APP_ID";
    public static final String awsSenderEmail = "AWS_SENDER_EMAIL";
    public static final String awsSenderPhone = "AWS_SENDER_PHONE";
    public static final String awsEmailCharset = "AWS_EMAIL_CHARSET";
    public static final String awsEmailCharsetDefault = "UTF-8";
    public static final String awsSmsMessageType = "AWS_SMS_MESSAGE_TYPE";
    public static final String awsSmsMessageTypeDefault = "TRANSACTIONAL";
    public static final String awsSmsSenderId = "AWS_SMS_SENDER_ID";
    public static final String awsSmsSenderIdDefault = "MegaBytes";

    public String getUrlAddress() {
        return getenv(urlAddress, urlAddressDefault);
    }

    public String getJwtSecret() {
        return getenv(jwtSecret, jwtSecretDefault);
    }

    public String getSendGridApiKey() {
        return getenv(sendGridApiKey, "UNSPECIFIED");
    }

    public String getSendGridEmail() {
        return getenv(sendGridEmail, "UNSPECIFIED");
    }

    public String getAwsAccessKeyId() {
        return getenv(awsAccessKeyId, "UNSPECIFIED");
    }

    public String getAwsSecretAccessKey() {
        return getenv(awsSecretAccessKey, "UNSPECIFIED");
    }

    public String getAwsPinpointAppId() {
        return getenv(awsPinpointAppId, "UNSPECIFIED");
    }

    public String getAwsSenderEmail() {
        return getenv(awsSenderEmail, "UNSPECIFIED");
    }

    public String getAwsSenderPhone() {
        return getenv(awsSenderPhone, "UNSPECIFIED");
    }

    public String getAwsEmailCharset() {
        return getenv(awsEmailCharset, awsEmailCharsetDefault);
    }

    public String getAwsSmsMessageType() {
        return getenv(awsSmsMessageType, awsSmsMessageTypeDefault);
    }

    public String getAwsSmsSenderId() {
        return getenv(awsSmsSenderId, awsSmsSenderIdDefault);
    }

    // Provides a fail-safe means of querying environment variables.
    public String getenv(String name, String defaultValue) {
        String result = null;
        try {
            result = System.getenv(name);
        } catch (NullPointerException | SecurityException e) {
            String msg = String.format("Environment variable \"%s\" not found. "
            + "Value will be \"%s\".", name, defaultValue);
            System.err.println(msg);
        }

        if (result == null)
            result = defaultValue;

        return result;
    }
}
