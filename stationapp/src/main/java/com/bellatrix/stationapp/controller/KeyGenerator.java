package com.bellatrix.stationapp.controller;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class KeyGenerator {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HS256 est suffisant, mais HS512 est plus fort
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Generated Base64 Key: " + base64Key);
    }
}