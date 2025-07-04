package com.hospital.Hospital_Management_System.config;

import com.hospital.Hospital_Management_System.config.security.jwt.AuthTokenFilter;
import com.hospital.Hospital_Management_System.config.security.jwt.JwtAuthEntryPoint;
import com.hospital.Hospital_Management_System.config.security.jwt.JwtUtils;
import com.hospital.Hospital_Management_System.config.security.userService.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration {

    private final CustomerUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final JwtAuthEntryPoint authEntryPoint;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return  new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(getPasswordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer :: disable)

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authEntryPoint))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",       // السماح لـ OpenAPI Docs
                                "/swagger-ui/**",        // السماح لـ Swagger UI
                                "/swagger-ui.html"       // السماح بصفحة Swagger
                        ).permitAll()

                        .requestMatchers("/api/patients/**")
                        .hasAnyAuthority("USER" , "ADMIN")

                        .requestMatchers("/api/doctors/**")
                        .hasAnyAuthority("ADMIN")

                        .requestMatchers("/api/appointments/**")
                        .hasAnyAuthority("USER" , "ADMIN")

                        .requestMatchers("/api/rooms/**")
                        .hasAnyAuthority("USER","ADMIN")

                        .anyRequest()
                        .permitAll()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authTokenFilter() , UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
