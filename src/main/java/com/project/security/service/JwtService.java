package com.project.security.service;

import com.project.security.entity.dto.CustomClaimDto;
import com.project.security.entity.model.User;
import com.project.security.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Service
@NoArgsConstructor
@Getter
public class JwtService {

    @Value("${access.token.secret.key}")
    private String accessTokenSecretKey;

    @Value("${access.token.expiry}")
    private String accessTokenExpiry;

    @Value("${refresh.token.secret.key}")
    private String refreshTokenSecretKey;

    @Value("${refresh.token.expiry}")
    private String refreshTokenExpiry;

    private ObjectMapper objectMapper;

    public SecretKey getAccessTokenSecretKey() {
        return Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getRefreshTokenSecretKey() {
        return  Keys.hmacShaKeyFor(refreshTokenSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user, TokenType tokenType){

        CustomClaimDto customClaimDto = new CustomClaimDto();
        customClaimDto.setUsername(user.getUsername());
        customClaimDto.setIsAccountNonLocked(user.isAccountNonLocked());
        customClaimDto.setCreatedAt(user.getCreatedAt());
        customClaimDto.setName(user.getName());
        customClaimDto.setUpdatedAt(user.getUpdatedAt());
        customClaimDto.setRoles( user.getRoles().stream().map(r -> r.getName()).toList());

        Map<String, Object> userClaim = objectMapper.convertValue(customClaimDto, Map.class);

        String expiry =
                tokenType.equals(TokenType.ACCESS_TOKEN) ? accessTokenExpiry : refreshTokenExpiry;
        SecretKey secretKey =
                tokenType.equals(TokenType.ACCESS_TOKEN) ? getAccessTokenSecretKey() : getRefreshTokenSecretKey();

        return Jwts
                .builder()
                .subject(user.getUsername())
                .signWith(secretKey)
                .claim("userClaim", userClaim)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .compact();
    }

    public Claims parseFromToken(String token, TokenType tokenType){
        SecretKey secretKey =
                tokenType.equals(TokenType.ACCESS_TOKEN) ? getAccessTokenSecretKey() : getRefreshTokenSecretKey();

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
