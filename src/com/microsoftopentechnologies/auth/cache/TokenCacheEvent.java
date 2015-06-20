package com.microsoftopentechnologies.auth.cache;

import java.util.EventObject;

public class TokenCacheEvent extends EventObject {
    private TokenCache tokenCache;
    private String clientId;
    private String resource;
    private String uniqueId;
    private String displayableId;

    public TokenCacheEvent(Object source) {
        super(source);
    }

    public TokenCacheEvent(Object source, TokenCache tokenCache, String clientId, String resource, String uniqueId, String displayableId) {
        super(source);
        this.tokenCache = tokenCache;
        this.clientId = clientId;
        this.resource = resource;
        this.uniqueId = uniqueId;
        this.displayableId = displayableId;
    }

    public TokenCache getTokenCache() {
        return tokenCache;
    }

    public void setTokenCache(TokenCache tokenCache) {
        this.tokenCache = tokenCache;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDisplayableId() {
        return displayableId;
    }

    public void setDisplayableId(String displayableId) {
        this.displayableId = displayableId;
    }
}
