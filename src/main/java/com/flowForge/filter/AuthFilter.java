package com.flowForge.filter;

import com.flowForge.entity.dto.CustomClaimDto;
import com.flowForge.entity.model.User;
import com.flowForge.enums.TokenType;
import com.flowForge.repository.UserRepository;
import com.flowForge.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
    return request.getRequestURI().startsWith("/api/v1/auth")
            || request.getRequestURI().startsWith("/api/v1/role");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if( cookies==null || cookies.length ==0 ){
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = "";
        for (Cookie value : cookies) {
            if (value.getName().equals("access-token")) {
                accessToken = value.getValue();
            }
        }

        if( accessToken.isBlank() ){
            filterChain.doFilter(request, response);
            return;
        }
        Claims claims = null;
        try {
            claims = jwtService.parseFromToken(accessToken, TokenType.ACCESS_TOKEN);
        }catch (ExpiredJwtException e){

            User user  = userRepository.getUserByEmail(e.getClaims().getSubject());

            Claims refreshTokenClaims = null;

            try {
                refreshTokenClaims =
                        jwtService.parseFromToken(user.getRefreshToken(), TokenType.REFRESH_TOKEN);
            }catch (ExpiredJwtException ex ){
                filterChain.doFilter(request, response);
                return;
            }

            String newAccessToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);

            user.setRefreshToken(jwtService.generateToken(user, TokenType.REFRESH_TOKEN));
            userRepository.save(user);

            ResponseCookie responseCookie = ResponseCookie.from("access-token", newAccessToken)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(60*60*24*4)
                    .path("/")
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            claims = jwtService.parseFromToken(newAccessToken, TokenType.ACCESS_TOKEN);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        if( claims==null || claims.isEmpty() ) {
            filterChain.doFilter(request, response);
            return;
        }

        CustomClaimDto userClaims = objectMapper.convertValue(claims.get("userClaim"), CustomClaimDto.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userClaims.getUsername(),
                null,
                userClaims.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
