package com.bifee.projectmanagement.identity.infrastructure.security;

import com.bifee.projectmanagement.identity.domain.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            JWSSigner signer = new MACSigner(secret);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer("project-management-api")
                    .subject(user.email().value())
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(7200)))
                    .claim("id", user.id())
                    .claim("role", user.role().name())
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate jwt token", e);
        }
    }
}