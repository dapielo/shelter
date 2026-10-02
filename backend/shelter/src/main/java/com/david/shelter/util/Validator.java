package com.david.shelter.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
