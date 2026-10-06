package com.david.shelter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

public class Validator {

    
    public static boolean isValidEmail(String email) {
        // Expresion regular uqe verifica si un correo electronico es valido
        String emailRegex = "^[a-zA-Z0-9]+[\\w\\.%+-]*@.+\\.[a-zA-Z]{2,}$";
        // Compilamos la expresion regular y creamos el matcher
        Pattern emailPatter = Pattern.compile(emailRegex);
        Matcher emailMatcher = emailPatter.matcher(email);
        // devuelve true si cumple la expresion regular
        return emailMatcher.matches();
    }

    public static boolean isValidImage(MultipartFile file) {
        
        String name = file.getOriginalFilename();

        if (name == null) {
            return false;
        } else {
            name = name.toLowerCase();
        }
        if (name.isBlank()) {
            return false;
        }
        if (name.endsWith(".jpeg")) {
            return true;
        }
        if (name.endsWith(".jpg")) {
            return true;
        }
        if (name.endsWith(".png")) {
            return true;
        }
        return false;
    }
}
