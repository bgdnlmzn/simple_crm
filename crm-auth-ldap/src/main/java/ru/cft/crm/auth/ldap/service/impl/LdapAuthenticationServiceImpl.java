package ru.cft.crm.auth.ldap.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Service;
import ru.cft.crm.auth.ldap.exception.LdapAuthenticationException;
import ru.cft.crm.auth.ldap.model.AuthenticationRequest;
import ru.cft.crm.auth.ldap.model.AuthenticationResponse;
import ru.cft.crm.auth.ldap.service.LdapAuthenticationService;
import ru.cft.crm.auth.security.jwt.JwtTokenProvider;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LdapAuthenticationServiceImpl implements LdapAuthenticationService {

    private final LdapAuthenticationProvider ldapAuthenticationProvider;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            log.info("Attempting LDAP authentication for user: {}", request.getUsername());

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication authentication = ldapAuthenticationProvider.authenticate(authRequest);

            if (!authentication.isAuthenticated()) {
                throw new LdapAuthenticationException("LDAP authentication failed");
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            log.info("User {} authenticated successfully with roles: {}", request.getUsername(), roles);

            String token = jwtTokenProvider.createToken(authentication);

            return AuthenticationResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .username(authentication.getName())
                    .authorities(roles)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw new LdapAuthenticationException("Invalid username or password");
        } catch (Exception e) {
            log.error("LDAP authentication error for user: {}", request.getUsername(), e);
            throw new LdapAuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}

