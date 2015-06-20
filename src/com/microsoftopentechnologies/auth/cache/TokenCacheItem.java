package com.microsoftopentechnologies.auth.cache;

import com.microsoftopentechnologies.auth.AuthenticationResult;

public class TokenCacheItem {
    private String authority;
    private String clientId;
    private String displayableId;
    private String resource;
    private String uniqueId;
    private AuthenticationResult authenticationResult;

    public TokenCacheItem(TokenCacheKey tokenCacheKey, AuthenticationResult authenticationResult) {
        authority = tokenCacheKey.getAuthority();
        clientId = tokenCacheKey.getClientId();
        displayableId = tokenCacheKey.getDisplayableId();
        resource = tokenCacheKey.getResource();
        uniqueId = tokenCacheKey.getUniqueId();
        this.authenticationResult = authenticationResult;
    }

    public String getAuthority() {
        return authority;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDisplayableId() {
        return displayableId;
    }

    public String getResource() {
        return resource;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
}
