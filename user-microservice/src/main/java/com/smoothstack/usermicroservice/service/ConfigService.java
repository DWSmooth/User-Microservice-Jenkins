package com.smoothstack.usermicroservice.service;

import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    public static final String urlAddressName = "URL_ADDRESS";
    public static final String urlAddressValue = "http://localhost:8080/";

    public static final String jwtSecretName = "JWT_SECRET";
    public static final String jwtSecretValue = "testSecret123";

    public static final String sendGridApiKeyName = "SENDGRID_API_KEY";
    public static final String sendGridApiKeyValue = "MUST_SPECIFY";

    public static final String sendGridEmailName = "SENDGRID_EMAIL";
    public static final String sendGridEmailValue = "MUST_SPECIFY";

    public String getUrlAddress() {
        return getenv(urlAddressName, urlAddressValue);
    }

    public String getJwtSecret() {
        return getenv(jwtSecretName, jwtSecretValue);
    }

    public String getSendGridApiKey() {
        return getenv(sendGridApiKeyName, sendGridApiKeyValue);
    }

    public String getSendGridEmail() {
        return getenv(sendGridEmailName, sendGridEmailValue);
    }

    // Provides a fail-safe means of querying environment variables.
    public String getenv(String name, String defaultValue) {
        String result;
        try {
            result = System.getenv(name);
        } catch (NullPointerException | SecurityException e) {
            result = defaultValue;
            String msg = String.format("Environment variable \"%s\" not found. "
            + "Value will be \"%s\".", name, result);
            System.err.println(msg);
        }

        return result;
    }
}
