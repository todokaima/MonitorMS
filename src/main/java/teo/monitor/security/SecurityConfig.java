package teo.monitor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/stream").permitAll()  // SSE endpoint stays public
                        .anyRequest().authenticated()            // everything else secured
                )
                .formLogin(form -> form.permitAll())        // form login
                .httpBasic(httpBasic -> {});               // lambda required, empty if no customization

        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}password") // no encoder for simplicity
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
