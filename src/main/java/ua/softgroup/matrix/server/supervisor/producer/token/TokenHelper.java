package ua.softgroup.matrix.server.supervisor.producer.token;

import com.google.common.io.ByteStreams;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ua.softgroup.matrix.server.supervisor.producer.exception.JwtException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
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
@Component
public class TokenHelper {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String ALGORITHM = "RS512";

    private static final String PUBLIC_KEY_FILE = "public_key.der";
    private static final String PRIVATE_KEY_FILE = "private_key.der";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws IOException, GeneralSecurityException {
        publicKey = generatePublic(securityKey2Bytes(PUBLIC_KEY_FILE));
        privateKey = generatePrivate(securityKey2Bytes(PRIVATE_KEY_FILE));
    }

    private byte[] securityKey2Bytes(String filePath) throws IOException {
        try (InputStream is = new ClassPathResource(filePath).getInputStream()) {
            return ByteStreams.toByteArray(is);
        }
    }

    private PublicKey generatePublic(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private PrivateKey generatePrivate(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    public String generateToken(String subject, String issuer, Date expirationTime) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.parse(ALGORITHM)),
                new JWTClaimsSet.Builder()
                        .subject(subject)
                        .issuer(issuer)
                        .expirationTime(expirationTime)
                        .build());
        signedJWT.sign(new RSASSASigner(privateKey));
        return signedJWT.serialize();
    }

    public boolean validateToken(String token) {
        try {
            RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
            return SignedJWT.parse(token).verify(verifier);
        } catch (JOSEException | ParseException e) {
            throw new JwtException(e);
        }
    }

    public String extractSubjectFromToken(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new JwtException(e);
        }
    }

}
