package com.blackops.securitydemo.config;

import com.blackops.securitydemo.user.Role;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor //hides the impl for DI, private-final fields will be autowired but we wont se it
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

//    @Bean
//    @Order(1)
//    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity.csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests((requests)->
//                        requests.requestMatchers("/admin/**")
//                                .hasRole(Role.ADMIN.name())
//                )
//                .build();
//    }

//    @Bean
//    @Order(1)
//    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .securityMatcher("/admin/**")
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().hasRole(Role.ADMIN.name())
//                )
//                //Because we want each request to be authenticated, the session should be with no state(OncePerRequest, not per session)
//                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider)
//                // Add our filter before
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }


    //When load the request context in our JWT filter, spring will need a filterchain bean
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests)->
                        requests.requestMatchers("/api/v1/auth/**")
                                .permitAll()
                                .requestMatchers("/admin/**")
                                .hasRole(Role.ADMIN.name())
                                .anyRequest()
                                .authenticated()
                )
                //Because we want each request to be authenticated, the session should be with no state(OncePerRequest, not per session)
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                // Add our filter before
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
