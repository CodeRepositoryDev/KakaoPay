package com.kakao.pay.sprinkle.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private static final int TOKEN_SIZE = 3;

    public String getToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_SIZE);
    }
}
