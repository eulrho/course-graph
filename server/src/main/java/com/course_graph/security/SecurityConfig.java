package com.course_graph.security;

import com.course_graph.Exception.CustomAccessDeniedHandler;
import com.course_graph.Exception.CustomAuthenticationEntryPoint;
import com.course_graph.repository.TokenRepository;
import com.course_graph.token.JwtAuthenticationFilter;
import com.course_graph.token.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final TokenRepository tokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement)->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/api/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/send-mail")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/verify-mail")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/subject-upload")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/curriculum-upload")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/graduation-upload")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/equivalence-upload")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/schedule-upload")).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, tokenRepository), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(jwtProvider, tokenRepository))
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
//                        );
        return http.build();
    }
}
