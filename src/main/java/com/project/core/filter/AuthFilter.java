package com.project.core.filter;

import com.project.core.entity.dto.CustomClaimDto;
import com.project.core.entity.model.User;
import com.project.core.enums.TokenType;
import com.project.core.repository.UserRepository;
import com.project.core.service.JwtService;
import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;


    @Override
        protected boolean shouldNotFilter(HttpServletRequest request){
        return request.getRequestURI().startsWith("/api/v1/auth") || request.getContextPath().startsWith("/api/v1/role");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookie = request.getCookies();

        if( cookie==null || cookie.length ==0 ){
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = "";
        for (Cookie value : cookie) {
            if (value.getName().equals("access-token")) {
                accessToken = value.getValue();
            }
        }

        if( accessToken.isBlank() ){
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.parseFromToken(accessToken, TokenType.ACCESS_TOKEN);

        if(claims.isEmpty() ) {
            filterChain.doFilter(request, response);
            return;
        }

        if( claims.getExpiration().before(new Date(System.currentTimeMillis())) ){
            User user  = userRepository.getUserByEmail(claims.getSubject());
            Claims refreshTokenClaims = jwtService.parseFromToken(user.getRefreshToken(), TokenType.REFRESH_TOKEN);

            if( refreshTokenClaims.isEmpty() || refreshTokenClaims.getExpiration().before(new Date(System.currentTimeMillis())) ){
                filterChain.doFilter(request, response);
                return;
            }

            String newAccessToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);

            ResponseCookie responseCookie = ResponseCookie.from("access-token", newAccessToken)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(60*60*24*4)
                    .path("/")
                    .build();
            response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            claims = jwtService.parseFromToken(newAccessToken, TokenType.ACCESS_TOKEN);
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
