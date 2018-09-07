package com.github.imflog.res.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@KeycloakConfiguration
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

//    @Value("${access-control-max-age}")
//    private Long accessControlMaxAge;

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keyCloakAuthProvider = keycloakAuthenticationProvider();
        keyCloakAuthProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keyCloakAuthProvider);
    }

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    // TODO : handle CORS correctly
/*    private CorsConfiguration corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(false);
        configuration.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setMaxAge(accessControlMaxAge);
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.addAllowedMethod(HttpMethod.HEAD);
        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.POST);
        configuration.addAllowedMethod(HttpMethod.DELETE);
        configuration.addAllowedMethod(HttpMethod.PATCH);
        return configuration;
    }

    private CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return source;
    }

    private Filter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .sessionManagement()
                // null session
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // Should we add the cors filter ?
                // .addFilterBefore(corsFilter(), ChannelProcessingFilter.class);

                // keycloak filters for securisation
                .and()
                .addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
                // Filter responsible of the Token validation, see {@link org.keycloak.adapters.BearerTokenRequestAuthenticator}
                .addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                // delegate logout endpoint to Spring Security
                .and()
                .logout()
                .addLogoutHandler(keycloakLogoutHandler())
                .logoutUrl("/logout")
                .logoutSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) ->
                        response.setStatus(HttpServletResponse.SC_OK))

                // Manage route authorizations
                .and()
                // CSRF no required in the API
                .csrf().disable()

                // manage routes securisation
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll() // For node JS
                .antMatchers("/identity/verify").permitAll()
                // allow all requests to some of the actuator endpoints - including pass-through requests to
                // other services
                .antMatchers("/health").permitAll()
                .antMatchers("/info").permitAll()
                .anyRequest().permitAll();
    }
}