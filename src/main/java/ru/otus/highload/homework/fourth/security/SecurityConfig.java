package ru.otus.highload.homework.fourth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.otus.highload.homework.fourth.service.UserDetail;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final UserDetail userDetail;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private final UnauthorizedEntrypoint unauthorizedEntrypoint;

    @Bean
    public SecurityFilterChain filterSecurity(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.securityMatcher("/user/register","/user/get/**","/login", "/post/feed", "/chat/get/with_user/**")
                .authorizeHttpRequests(
                        (authorize) -> authorize
                                .requestMatchers("/user/get/**").authenticated()
                                .requestMatchers("/friend/set").authenticated()
                                .requestMatchers("/friend/delete").authenticated()
                                .requestMatchers("/post/feed").authenticated()
                                .requestMatchers("/user/register").permitAll()
                                .requestMatchers("/user/search").permitAll()
                                .requestMatchers("/chat/get/with_user/**").authenticated()
                                .requestMatchers("/login").authenticated()
                                .anyRequest().authenticated())


                .httpBasic(httpSec -> httpSec.authenticationEntryPoint(unauthorizedEntrypoint)).userDetailsService(userDetail);
        return http.build();
    }
}
