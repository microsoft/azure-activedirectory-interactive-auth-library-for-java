package com.microsoftopentechnologies.auth.cache;

public class AfterWriteEvent extends TokenCacheEvent {
    public AfterWriteEvent(Object source) {
        super(source);
    }

    public AfterWriteEvent(Object source, TokenCache tokenCache, String clientId, String resource, String uniqueId, String displayableId) {
        super(source, tokenCache, clientId, resource, uniqueId, displayableId);
    }

    public AfterWriteEvent(Object source, TokenCacheEvent tokenCacheEvent) {
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
