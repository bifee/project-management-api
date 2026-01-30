package com.bifee.projectmanagement.identity.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bifee.projectmanagement.identity.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;


@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try{
           Algorithm algorithm = Algorithm.HMAC256(secret);
           String token = JWT.create()
                   .withIssuer("auth-api")
                   .withSubject(user.email().value())
                   .withExpiresAt(this.genExpirationDate())
                   .sign(algorithm);
           return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error creating JWT", exception);
        }
    }

    public String validateToken(String token) {
        if (token == null || token.isBlank()) return null;
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();
        }
        catch (JWTVerificationException exception) {
            return null;
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(java.time.ZoneOffset.UTC);
    }
}