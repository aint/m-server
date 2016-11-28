package ua.softgroup.matrix.server.supervisor.jersey.crypto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class KeyHelper {

    public static final String SECRET_WORD = "Mrazish";

    private static final String KEY_FILE = "keystore.jks";
    private static final char[] PASSWORD = "password".toCharArray();
    private static final String ALIAS = "selfsigned";

    public static Key getKey() throws IOException, GeneralSecurityException {
        try (InputStream readStream = new FileInputStream(KEY_FILE)) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(readStream, PASSWORD);
            return keyStore.getKey(ALIAS, PASSWORD);
        }
    }

    public static String generateToken(Key key) {
        return Jwts.builder()
                .setSubject(SECRET_WORD)
                .signWith(SignatureAlgorithm.RS512, key)
                .compact();
    }

//    keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048

//    OK:   eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJNcmF6aXNoIn0.Pm58O29gw9y5W59_ISHKun8wpIyCtS6Nnp7SEU8i-pDOlVfh389sHnvqDsuWgjSzomALbOe2xv7rtQm09mUfuB7po0vLCRc1CZ5VSJQFqeyp9n-7rq__2ay3TlxxmCwN_1uCt2gtvrmI5t19pbnjVWDdyHd8zDtSYeOAjLu30fNqiRe5twFJWQcrAXjJvww3hiMOi3APT2YA8leKdMTiXop6eHnfr7RjWMXidnXom9qIN7dFgD-8_dx1eEqjboxWN9TOOoUcN0vz1mFSAMVqoEwlcBMVFL06XIBZ3ySrmpam-PVEF0XfHDacfMd7lbQoslt2tdLW1F9h7__c7WK4xA

//    FAIL: eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJNcmF6aXNoODgifQ.ZzBn1GjMKFl4Z4AsxPfC1--K2REXdCD3yvFEzTVmzae8LrKTn5AsbOcTd-Y1mlW2fFJq9UI4HS8R_Fy-dtKhFYo5S7hYIpvT11mKXvrvjUGXijuz8fOL47ZIu6hc_qZCSWS7_fRIhaAJsonkSfhVFDwgHpN_iSkfgHYHVdNo2TRJt4cnsQSHS5BRBHyfcpCAM0Cp_AOuzvNzUoW6Zfye4JQwaeaKi5BlpBQkpe1aEkeuUHGgJQdulFfHvLAo9gqslIc-ya8byBiYXOppOrCPcvALinH4BPPbjkWxOhmEbNq4O--4tXcY7IrZGGTfcj1fwDWutGTb438FV2tpLhEEJg
}
