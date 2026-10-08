package com.nrvsocial.urlshortener.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    public String generate() {
        String key = "xxxxxx";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(key);

        char[] alphaNumeric = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        };


        for (int i = 0; i < key.length(); i++) {
            int randomNumber = random.nextInt(alphaNumeric.length);
            sb.setCharAt(i, alphaNumeric[randomNumber]);
        }

        return sb.toString();
    }
}
