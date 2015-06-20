package com.microsoftopentechnologies.auth.cache;

public class BeforeAccessEvent extends TokenCacheEvent {
    public BeforeAccessEvent(Object source) {
        super(source);
    }

    public BeforeAccessEvent(Object source, TokenCache tokenCache, String clientId, String resource, String uniqueId, String displayableId) {
        super(source, tokenCache, clientId, resource, uniqueId, displayableId);
    }

    public BeforeAccessEvent(Object source, TokenCacheEvent tokenCacheEvent) {
        this(
                source,
                tokenCacheEvent.getTokenCache(),
                tokenCacheEvent.getClientId(),
                tokenCacheEvent.getResource(),
                tokenCacheEvent.getUniqueId(),
                tokenCacheEvent.getDisplayableId()
        );
    }
}
