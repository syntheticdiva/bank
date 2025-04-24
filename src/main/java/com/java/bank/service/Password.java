//package com.java.bank.service;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//
//public class Password {
//    public static void main(String[] args) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
//
//        String[] passwords = {
//                "SecurePass123!",
//                "SecurePass123!",
//                "SecurePass123!",
//                "SecurePass123!",
//                "SecurePass123!"
//        };
//
//        for (String password : passwords) {
//            String hash = encoder.encode(password);
//            System.out.println(password + " → " + hash);
//        }
//    }
//}
