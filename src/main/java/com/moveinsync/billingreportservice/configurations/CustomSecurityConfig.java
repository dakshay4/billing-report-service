package com.moveinsync.billingreportservice.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {

  @Bean

  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()).authorizeRequests(auth -> auth.requestMatchers("/").permitAll()).httpBasic(withDefaults()).build();
  }

}