package com.smoothstack.usermicroservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.smoothstack.usermicroservice.data.jwt.ConfirmEmailToken;
import com.smoothstack.usermicroservice.data.jwt.ResetPasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private String jwtSecret;

    private Algorithm algorithm;

    private JWTVerifier verifier;

    @Autowired
    public JwtService(ConfigService config) {
        this.jwtSecret = config.getJwtSecret();
        this.algorithm = Algorithm.HMAC256(this.jwtSecret);
        this.verifier = JWT.require(this.algorithm).build();
    }

    public String generateToken(ConfirmEmailToken token)
            throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject(token.getUserId().toString())
                .withExpiresAt(token.getExpiry())
                .sign(this.algorithm);

    }

    public String generateToken(ResetPasswordToken token)
            throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject(token.getUserId().toString())
                .withJWTId(token.getConfirmation())
                .withExpiresAt(token.getExpiry())
                .sign(this.algorithm);
    }

    public ConfirmEmailToken validateConfirmEmailToken(String token)
            throws JWTVerificationException {
        DecodedJWT jwt = this.verifier.verify(token);
        ConfirmEmailToken output = new ConfirmEmailToken();
        output.setUserId(Integer.parseInt(jwt.getSubject()));
        output.setExpiry(jwt.getExpiresAt());
        return output;
    }

    public ResetPasswordToken validateResetPasswordToken(String token)
            throws JWTVerificationException {
        DecodedJWT jwt = this.verifier.verify(token);
        ResetPasswordToken output = new ResetPasswordToken();
        output.setUserId(Integer.parseInt(jwt.getSubject()));
        output.setConfirmation(jwt.getId());
        output.setExpiry(jwt.getExpiresAt());
        return output;
    }
}
