package com.course_graph.Exception;

import com.course_graph.enums.CustomErrorCode;
import com.course_graph.token.JwtAuthenticationFilter;
import com.course_graph.token.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final JwtProvider jwtProvider;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        final String token = jwtProvider.resolveToken(request);
        ErrorCode errorCode;

        if (token == null || token.length() < 8 || !token.substring(0, "BEARER ".length()).equalsIgnoreCase("BEARER ")) {
            errorCode = CustomErrorCode.INVALID_TOKEN;
            JwtAuthenticationFilter.setErrorResponse(response, errorCode);
        }
        else if (jwtProvider.isExpired(token)) {
            errorCode = CustomErrorCode.EXPIRED_TOKEN;
            JwtAuthenticationFilter.setErrorResponse(response, errorCode);
        }
    }
}
