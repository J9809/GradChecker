package com.gradchecker.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class security {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/registerMember", "/findPassword").permitAll() // 특정 URL에 대해 모든 사용자에게 허용
                                .anyRequest().authenticated() // 나머지 모든 요청에는 인증이 필요
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // 사용자 정의 로그인 페이지 지정
                                .permitAll() // 로그인 페이지는 모든 사용자에게 허용
                )
                .logout(logout ->
                        logout
                                .permitAll() // 로그아웃은 모든 사용자에게 허용
                );

        return http.build();
    }
}
