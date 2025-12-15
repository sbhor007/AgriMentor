package com.server.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.server.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
	@Value("${jwt.secret-key}")
	private String JWTSecretKey;

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(JWTSecretKey.getBytes(StandardCharsets.UTF_8));
	}

	public String generatAccessToken(User user) {
		log.info("Generating JWT token for user: {}", user.getEmail());
		log.info("secret key: {}", JWTSecretKey);
		String role = user.getRole() != null ? user.getRole().name() : "UNKNOWN";
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("UserId", user.getId().toString())
				.claim("role", role)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 10000 * 60 * 10))
				.signWith(getSecretKey())
				.compact();
	}

	// extract email and roll from token
	public String getUsernameFromToken(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}

	private Claims getAllClaimsFromToken(String token) {
		// Parse JWT using the secret key and extract payload (Claims)
		return Jwts.parser()
				.verifyWith(getSecretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	// Extract Role from JWT token
	public String getRoleFromToken(String token) {
		return getAllClaimsFromToken(token).get("role", String.class);
	}

}
