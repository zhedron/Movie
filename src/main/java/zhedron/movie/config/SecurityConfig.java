package zhedron.movie.config;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zhedron.movie.config.filter.JwtFilter;
import zhedron.movie.services.impl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    /*private final String[] PUBLIC_ENDPOINTS = {
            "/api/user/registration",
            "/api/login",
            "/api/mediacontent/{id}",
            "/api/mediacontent",
            "/api/mediacontent/start-date/end-date",
            "/api/season/{id}",
            "/api/film/{id}",
            "/api/episode/{id}"
    };

    private final String[] ADMIN_ENDPOINTS = {
            "/api/mediacontent/create",
            "/api/mediacontent/delete/{id}",
            "/api/mediacontent/change-status/{id}",
            "/api/mediacontent/film",
            "/api/mediacontent/season",
            "/api/mediacontent/film/delete",
            "/api/mediacontent/season/delete",
            "/api/mediacontent/update/{id}",
            "/api/season/create",
            "/api/season/delete/{id}",
            "/api/film/upload",
            "/api/film/delete/{id}",
            "/api/episode/upload",
            "/api/episode/delete/{id}"
    };*/

    private final String[] PUBLIC_ENDPOINTS = {
            "/api/user/registration",
            "/api/login",
            "/api/mediacontent/{id}",
            "/api/mediacontent",
            "/api/mediacontent/start-date/end-date",
            "/api/season/*",
            "/api/episode/*",
            "/api/film/{id:\\d}",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator",
    };

    private final String[] ADMIN_ENDPOINTS = {
            "/api/mediacontent/**",
            "/api/season/**",
            "/api/film/**",
            "/api/episode/**",
            "/api/user/change-role/*"
    };

    private final String[] AUTHORIZED_ENDPOINTS = {
            "/api/comment/**"
    };

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                            .requestMatchers(ADMIN_ENDPOINTS).hasAuthority("ADMIN")
                            .requestMatchers(AUTHORIZED_ENDPOINTS).authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/comment/{id}/{mediaContentId}").permitAll();
                }).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
}
