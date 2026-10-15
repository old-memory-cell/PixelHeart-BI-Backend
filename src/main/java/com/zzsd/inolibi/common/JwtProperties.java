package com.zzsd.inolibi.common;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class JwtProperties {

    @Value("${jjwt.secret")
    private String SIGN_KEY_STR;

    @Value("${jjwt.expire")
    private Long expire;
}
