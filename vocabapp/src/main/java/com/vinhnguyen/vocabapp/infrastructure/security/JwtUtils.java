package com.vinhnguyen.vocabapp.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Lấy secret key từ file application.yml
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Lấy thời gian hết hạn từ file application.yml
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Hàm 1: Tạo ra vòng tay (Token) cho User
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Lưu username vào token
                .setIssuedAt(new Date()) // Thời gian phát hành
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Thời gian hết hạn
                .signWith(key(), SignatureAlgorithm.HS256) // Đóng dấu mộc bằng thuật toán HS256
                .compact();
    }

    // Hàm 2: Lấy Username từ cái Token (Để bảo vệ check xem ai đang giơ vòng tay)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Hàm 3: Kiểm tra xem Token có còn hạn và có hợp lệ không
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}