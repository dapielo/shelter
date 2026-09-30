package com.david.shelter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    
    public static boolean isValidEmail(String email) {
        
        String emailRegex = "^[a-zA-Z0-9]+[\\w\\.%+-]*@.+\\.[a-zA-Z]{2,}$";

        Pattern emailPatter = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPatter.matcher(email);
        
        return emailMatcher.matches();
    }
}
