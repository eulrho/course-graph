package com.course_graph.token;

import com.course_graph.Exception.ErrorCode;
import com.course_graph.entity.TokenEntity;
import com.course_graph.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();

        if (isPassURI(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtProvider.resolveToken(request);
        if (token != null && jwtProvider.validateToken(token)) {
            if (tokenRepository.findByToken(token.split(" ")[1].trim()).isPresent()) {
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    public boolean isPassURI(String requestURI) {
        return requestURI.equals("/api/join") || requestURI.equals("/api/login") || requestURI.equals("/api/send-mail")
                || requestURI.equals("/api/verify-mail") || requestURI.equals("/api/subject-upload") || requestURI.equals("/api/curriculum-upload")
                || requestURI.equals("/api/graduation-upload") || requestURI.equals("/api/equivalence-upload") || requestURI.equals("/api/schedule-upload");
    }

    public static void setErrorResponse(HttpServletResponse response, ErrorCode ErrorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("message", ErrorCode.getMessage());
        responseJson.put("code", ErrorCode.getHttpStatus());

        response.getWriter().print(responseJson);
    }
}
