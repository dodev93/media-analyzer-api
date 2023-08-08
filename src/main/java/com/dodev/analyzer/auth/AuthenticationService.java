package com.dodev.analyzer.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private final String authTokenHeaderName;
    private final String authToken;

    public AuthenticationService(@Value("${api.auth.token.header}") String authTokenHeaderName,
                                 @Value("${api.auth.token.value}") String authToken) {
        this.authTokenHeaderName = authTokenHeaderName;
        this.authToken = authToken;
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(authTokenHeaderName);

        if (apiKey == null)
            return null;

        if (!apiKey.equals(authToken)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.commaSeparatedStringToAuthorityList("API_KEY"));
    }
}
