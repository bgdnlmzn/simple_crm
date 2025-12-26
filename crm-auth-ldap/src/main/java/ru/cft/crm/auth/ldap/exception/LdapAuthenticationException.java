package ru.cft.crm.auth.ldap.exception;

public class LdapAuthenticationException extends RuntimeException {

    public LdapAuthenticationException(String message) {
        super(message);
    }

    public LdapAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

