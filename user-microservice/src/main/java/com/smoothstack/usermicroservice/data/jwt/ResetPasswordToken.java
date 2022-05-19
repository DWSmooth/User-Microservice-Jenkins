package com.smoothstack.usermicroservice.data.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordToken {

    private Integer userId;

    private String confirmation;

    private Date expiry;
}
