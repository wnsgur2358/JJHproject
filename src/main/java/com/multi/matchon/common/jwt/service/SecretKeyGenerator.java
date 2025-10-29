package com.multi.matchon.common.jwt.service;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        byte[] keyBytes = new byte[64]; // 64바이트 = 512비트
        SecureRandom random = new SecureRandom();
        random.nextBytes(keyBytes);

        String encodedSecretKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Base64 Encoded Secret Key: " + encodedSecretKey);
    }
}
