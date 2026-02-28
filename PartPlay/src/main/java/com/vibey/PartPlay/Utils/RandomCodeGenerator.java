package com.vibey.PartPlay.Utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCodeGenerator {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random random = new Random();

    public String generateCode(){
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<CODE_LENGTH; i++){
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }

        return sb.toString();
    }
}
