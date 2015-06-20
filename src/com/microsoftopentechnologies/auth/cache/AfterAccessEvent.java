package com.microsoftopentechnologies.auth.cache;

public class AfterAccessEvent extends TokenCacheEvent {
    public AfterAccessEvent(Object source) {
        super(source);
    }

    public AfterAccessEvent(Object source, TokenCache tokenCache, String clientId, String resource, String uniqueId, String displayableId) {
        super(source, tokenCache, clientId, resource, uniqueId, displayableId);
    }

    public AfterAccessEvent(Object source, TokenCacheEvent tokenCacheEvent) {
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
