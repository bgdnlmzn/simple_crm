package ru.cft.crm.auth.ldap.service;

import ru.cft.crm.auth.ldap.model.AuthenticationRequest;
import ru.cft.crm.auth.ldap.model.AuthenticationResponse;

public interface LdapAuthenticationService {

    AuthenticationResponse authenticate(AuthenticationRequest request);
}

