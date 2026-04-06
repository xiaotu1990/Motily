package com.motily.dns;

import java.security.SecureRandom;
import java.util.Base64;

public class DnsGenerator {
    private static final int DNS_LENGTH = 64;
    private static final SecureRandom random = new SecureRandom();

    public static String generateDns() {
        byte[] bytes = new byte[DNS_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateDnsFromParents(String fatherDns, String motherDns) {
        byte[] fatherBytes = Base64.getUrlDecoder().decode(fatherDns);
        byte[] motherBytes = Base64.getUrlDecoder().decode(motherDns);
        byte[] childBytes = new byte[DNS_LENGTH];

        for (int i = 0; i < DNS_LENGTH; i++) {
            if (random.nextDouble() < 0.45) {
                childBytes[i] = fatherBytes[i];
            } else if (random.nextDouble() < 0.9) {
                childBytes[i] = motherBytes[i];
            } else if (random.nextDouble() < 0.95) {
                // 社会环境影响
                childBytes[i] = (byte) (random.nextInt(256) - 128);
            } else {
                // 变异
                childBytes[i] = (byte) (random.nextInt(256) - 128);
            }
        }

        return Base64.getUrlEncoder().withoutPadding().encodeToString(childBytes);
    }
}
