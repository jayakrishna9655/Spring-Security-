package com.jai.SpringSecurity.Service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	private String secretKey="";
	
	public JWTService() {
		
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk=keyGen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
			System.out.println(">>> [JWTService CONSTRUCTOR] secretKey = " + secretKey);
			System.out.println(">>> [JWTService CONSTRUCTOR] this instance = " + this.hashCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String generateToken(String username) {
		Map<String, Object> clains = new HashMap<String, Object>();
		System.out.println(">>> [generateToken] secretKey = " + secretKey);
		System.out.println(">>> [generateToken] this instance = " + this.hashCode());
		String token = Jwts.builder().claims().add(clains).subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+60*60*30))
				.and().signWith(getKey()).compact();
		System.out.println(">>> [generateToken] generated token = " + token);
		return token;
	}

	private SecretKey getKey() {
		byte[] keybytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keybytes);
	}

	public String extractUserName(String token) {
		// extract the username from jwt token
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		System.out.println(">>> [extractAllClaims] secretKey = " + secretKey);
		System.out.println(">>> [extractAllClaims] this instance = " + this.hashCode());
		System.out.println(">>> [extractAllClaims] token = " + token);
		return Jwts.parser()
				.verifyWith(getKey())
				.build().parseClaimsJws(token).getBody();
	}
	
	public boolean validateToken(String token, UserDetails userDetails) {
		
		final String username=extractUserName(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpriration(token).before(new Date());
	}

	private Date extractExpriration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	
	
}
