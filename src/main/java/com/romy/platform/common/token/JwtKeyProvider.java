package com.romy.platform.common.token;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;


@Slf4j
@Component
public class JwtKeyProvider {

    @Value("${file.server-root-file-dir}")
    private String rootDir;

    private final String SECRET = "DoHwaENgneerIngtOkEn@n@gemEnTSrvc2";

    private Key hmacKey;
    private PrivateKey rsaPrivateKey;
    private PublicKey rsaPublicKey;
    private final boolean useRS256;

    public JwtKeyProvider(Environment env) {
        this.useRS256 = Arrays.stream(env.getActiveProfiles())
                              .anyMatch(profile -> !profile.equalsIgnoreCase("local"));
    }

    @PostConstruct
    private void initKeys() {
        try {
            if (this.useRS256) {
                String privKeyStr = this.readKeyFromFile(this.rootDir + "/Keys/jwt/private_key_pkcs8.pem");
                String pubKeyStr = this.readKeyFromFile(this.rootDir + "/Keys/jwt/public_key.pem");

                KeyFactory kf = KeyFactory.getInstance("RSA");
                this.rsaPrivateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(Decoders.BASE64.decode(this.stripPem(privKeyStr))));
                this.rsaPublicKey = kf.generatePublic(new X509EncodedKeySpec(Decoders.BASE64.decode(this.stripPem(pubKeyStr))));
            } else {
                this.hmacKey = Keys.hmacShaKeyFor(this.SECRET.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new RuntimeException("JWT 키 초기화 실패", e);
        }
    }

    private String readKeyFromFile(String path) throws Exception {
        return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
    }

    private String stripPem(String pem) {
        return pem.replaceAll("-----\\w+ PRIVATE KEY-----", "")
                  .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                  .replaceAll("\\s", "");
    }

    public boolean isRS256() {
        return this.useRS256;
    }

    public Key getSigningKey() {
        return this.isRS256() ? this.rsaPrivateKey : this.hmacKey;
    }

    public Key getVerifyingKey() {
        return this.isRS256() ? this.rsaPublicKey : this.hmacKey;
    }
}
