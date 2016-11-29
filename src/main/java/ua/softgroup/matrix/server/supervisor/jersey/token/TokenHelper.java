package ua.softgroup.matrix.server.supervisor.jersey.token;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
public class TokenHelper {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String ALGORITHM = "RS512";

    private static final String PUBLIC_KEY_FILE = "public_key.der";
    private static final String PRIVATE_KEY_FILE = "private_key.der";

    private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(PUBLIC_KEY_FILE).toPath());
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private static PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(new File(PRIVATE_KEY_FILE).toPath());
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private static String generateToken(JWSSigner signer) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.parse(ALGORITHM)),
                new JWTClaimsSet.Builder()
                        .subject("vadimb")
                        .issuer("http://example.com/")
                        .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                        .build());
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public static boolean validateToken(String token) throws GeneralSecurityException, ParseException, IOException, JOSEException {
        return token != null && SignedJWT.parse(token).verify(new RSASSAVerifier((RSAPublicKey) TokenHelper.getPublicKey()));
    }

    public static String extractSubjectFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    //    public static void main(String[] args) throws GeneralSecurityException, JOSEException, IOException, ParseException {
//        String token = generateToken(new RSASSASigner(getPrivateKey()));
//        System.out.println("TOKEN " + token);
//
//        SignedJWT signedJWT = SignedJWT.parse(token);
//        System.out.println("Validated " + validateToken(token));
//        System.out.println("Subject " + extractSubjectFromToken(token));
//        System.out.println("Issuer " + signedJWT.getJWTClaimsSet().getIssuer());
//        System.out.println(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
//    }
}
