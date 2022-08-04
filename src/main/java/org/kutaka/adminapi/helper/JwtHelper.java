package org.kutaka.adminapi.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.kutaka.adminapi.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
@Log4j2
public class JwtHelper {
  static final String issuer = "kutaka-admin";

  private final long accessTokenExpirationHours = 1;

  private Algorithm accessTokenAlgorithm;
  private JWTVerifier accessTokenVerifier;

  public JwtHelper() {
    accessTokenAlgorithm = Algorithm.HMAC512(System.getenv("JWT_ACCESS_TOKEN_SECRET"));
    accessTokenVerifier = JWT.require(accessTokenAlgorithm)
        .withIssuer(issuer)
        .build();
  }

  public String generateAccessToken(User user) {
    return JWT.create()
        .withIssuer(issuer)
        .withSubject(user.getId())
        .withIssuedAt(new Date())
        .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpirationHours * 60 * 60)))
        .sign(accessTokenAlgorithm);
  }

  private Optional<DecodedJWT> decodeAccessToken(String token) {
    try {
      return Optional.of(accessTokenVerifier.verify(token));
    } catch (JWTVerificationException e) {
      log.error("Access token is invalid", e);
    }
    return Optional.empty();
  }

  public boolean validateAccessToken(String token) {
    return decodeAccessToken(token).isPresent();
  }

  public String getUserIdFromAccessToken(String token) {
    return decodeAccessToken(token).get().getSubject();
  }
}