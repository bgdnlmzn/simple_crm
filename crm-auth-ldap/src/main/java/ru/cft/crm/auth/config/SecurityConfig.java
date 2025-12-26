package ru.cft.crm.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import ru.cft.crm.auth.security.jwt.JwtAuthenticationEntryPoint;
import ru.cft.crm.auth.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base-dn}")
    private String ldapBaseDn;

    @Value("${ldap.user-dn-pattern}")
    private String ldapUserDnPattern;

    @Value("${ldap.user-search-base}")
    private String ldapUserSearchBase;

    @Value("${ldap.user-search-filter}")
    private String ldapUserSearchFilter;

    @Value("${ldap.group-search-base}")
    private String ldapGroupSearchBase;

    @Value("${ldap.group-search-filter:member={0}}")
    private String ldapGroupSearchFilter;

    @Value("${ldap.group-role-attribute:cn}")
    private String ldapGroupRoleAttribute;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/dev/**",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource =
                new DefaultSpringSecurityContextSource(ldapUrl + "/" + ldapBaseDn);
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public BindAuthenticator bindAuthenticator(BaseLdapPathContextSource contextSource) {
        FilterBasedLdapUserSearch userSearch =
                new FilterBasedLdapUserSearch(ldapUserSearchBase, ldapUserSearchFilter, contextSource);

        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);
        authenticator.setUserDnPatterns(new String[]{ldapUserDnPattern});
        return authenticator;
    }

    @Bean
    public LdapAuthoritiesPopulator authoritiesPopulator(BaseLdapPathContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator populator =
                new DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase);
        populator.setGroupSearchFilter(ldapGroupSearchFilter);
        populator.setGroupRoleAttribute(ldapGroupRoleAttribute);
        populator.setRolePrefix("ROLE_");
        populator.setSearchSubtree(true);
        populator.setConvertToUpperCase(true);
        return populator;
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider(
            BindAuthenticator bindAuthenticator,
            LdapAuthoritiesPopulator authoritiesPopulator) {
        return new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
    }
}

