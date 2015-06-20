package com.microsoftopentechnologies.auth.cache;

public class BeforeWriteEvent extends TokenCacheEvent {
    public BeforeWriteEvent(Object source) {
        super(source);
    }

    public BeforeWriteEvent(Object source, TokenCache tokenCache, String clientId, String resource, String uniqueId, String displayableId) {
        super(source, tokenCache, clientId, resource, uniqueId, displayableId);
    }

    public BeforeWriteEvent(Object source, TokenCacheEvent tokenCacheEvent) {
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
