package com.paymybuddy.configuration;

import com.paymybuddy.security.JwtAuthFilter;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${jwt.key}")
    private String KEY;

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsServices;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsServices = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
                .headers(x->
                        x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x->
                        x
                                .requestMatchers("/registration/**","/login","/user/**","/","/css/**","/addNewUser","/auth/generateToken").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement( x ->  x.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authenticationProvider(authenticationProvider())
                .formLogin(formLoginConfigurer -> {
                    formLoginConfigurer.loginPage("/login").permitAll();
                    formLoginConfigurer.successForwardUrl("/home");
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer.invalidateHttpSession(true);
                    logoutConfigurer.clearAuthentication(true);
                    logoutConfigurer.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();
                    logoutConfigurer.logoutSuccessUrl("/login?logout").permitAll();
                })
                .rememberMe(x-> x.key(KEY));
                //.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                //.httpBasic(Customizer.withDefaults());

        return security.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsServices);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}


