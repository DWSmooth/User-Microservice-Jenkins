package com.smoothstack.usermicroservice.data.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmEmailToken {

    private Integer userId;

    private Date expiry;
}
