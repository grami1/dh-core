package com.grami1.dhcore.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@Profile("!test")
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableConfigurationProperties(WebSecurityProperties.class)
public class WebSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeExchange()
                    .pathMatchers(
                            "/swagger-ui.html",
                            "/webjars/swagger-ui/**",
                            "/v3/api-docs/**").permitAll()
                    .anyExchange().authenticated()
                .and()
                    .oauth2ResourceServer()
                        .jwt()
                .and()
                .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(((exchange, ex) -> Mono.error(ex)))
                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .logout().disable()
                    .httpBasic().disable()
                .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(WebSecurityProperties properties) {
        NimbusReactiveJwtDecoder reactiveJwtDecoder =
                (NimbusReactiveJwtDecoder) ReactiveJwtDecoders.fromOidcIssuerLocation(properties.issuerUri());

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(properties.audiences());
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(properties.issuerUri());
        JwtTimestampValidator jwtTimestampValidator = new JwtTimestampValidator();
        OAuth2TokenValidator<Jwt> fullValidator =
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator, jwtTimestampValidator);

        reactiveJwtDecoder.setJwtValidator(fullValidator);
        return reactiveJwtDecoder;
    }
}
