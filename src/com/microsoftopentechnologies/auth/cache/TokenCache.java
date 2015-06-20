package com.microsoftopentechnologies.auth.cache;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Closeables;
import com.google.gson.Gson;
import com.microsoftopentechnologies.auth.AuthenticationResult;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenCache {
    /**
     * This value is used to tag serialized representations of this cache with a version
     * number so we know we're only deserializing stuff that was serialized using this
     * implementation.
     */
    private final int SCHEMA_VERSION = 2;
    private final String DELIMITER = ":::";

    private static TokenCache instance;

    static {
        instance = new TokenCache();
    }

    /**
     * We do not want to return near expiry tokens, this is why we use this hard coded
     * setting to refresh tokens which are close to expiration.
     */
    private final int EXPIRATION_MARGIN_IN_MINUTES = 5;

    private Map<TokenCacheKey, AuthenticationResult> tokenCacheMap;
    private volatile boolean hasStateChanged = false;

    /**
     * Raises the following event types:
     *  {@link BeforeAccessEvent}
     *  {@link BeforeWriteEvent}
     *  {@link AfterAccessEvent}
     */
    private EventBus eventBus = new EventBus(TokenCache.class.getCanonicalName());

    public TokenCache() {
        tokenCacheMap = new ConcurrentHashMap<TokenCacheKey, AuthenticationResult>();
    }

    public static TokenCache getInstance() {
        return instance;
    }

    public TokenCache(byte[] state) throws IOException {
        this();
        deserialize(state);
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        Gson gson = new Gson();
        try {
            outputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeInt(SCHEMA_VERSION);
            objectOutputStream.writeInt(tokenCacheMap.size());
            for(Map.Entry<TokenCacheKey, AuthenticationResult> entry : tokenCacheMap.entrySet()) {
                objectOutputStream.writeChars(String.format("%s%s%s",
                        entry.getKey().getAuthority(),
                        DELIMITER,
                        entry.getKey().getResource()));
                gson.toJson(entry.getValue());
            }

            return outputStream.toByteArray();
        }
        finally {
            Closeables.close(objectOutputStream, true);
            Closeables.close(outputStream, true);
        }
    }

    public void deserialize(byte[] state) throws IOException {
        if(state == null) {
            tokenCacheMap.clear();
            return;
        }

        ByteArrayInputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        Gson gson = new Gson();

        try {
            inputStream = new ByteArrayInputStream(state);
            objectInputStream = new ObjectInputStream(inputStream);

            int schemaVersion = objectInputStream.readInt();
            if(schemaVersion != SCHEMA_VERSION) {
                System.out.printf("The version of the persistent state of the cache does not match the "+
                        "current schema, so skipping deserialization.");
                return;
            }

            tokenCacheMap.clear();
            int count = objectInputStream.readInt();
            for (int i = 0; i < count; i++) {
                String keyString = objectInputStream.readUTF();
                List<String> elements = Splitter.on(DELIMITER).splitToList(keyString);
                AuthenticationResult authenticationResult = gson.fromJson(
                        objectInputStream.readUTF(), AuthenticationResult.class);
                TokenCacheKey key = new TokenCacheKey(
                        elements.get(0), elements.get(1), authenticationResult.getUserInfo());

                tokenCacheMap.put(key, authenticationResult);
            }
        }
        finally {
            Closeables.closeQuietly(inputStream);
            Closeables.close(objectInputStream, true);
        }
    }

    public List<TokenCacheItem> readItems() {
        TokenCacheEvent tokenCacheEvent = new TokenCacheEvent(this);
        tokenCacheEvent.setTokenCache(this);

        eventBus.post(new BeforeAccessEvent(this, tokenCacheEvent));

        List<TokenCacheItem> items = new ArrayList<TokenCacheItem>(tokenCacheMap.size());
        for(Map.Entry<TokenCacheKey, AuthenticationResult> entry : tokenCacheMap.entrySet()) {
            items.add(new TokenCacheItem(entry.getKey(), entry.getValue()));
        }

        eventBus.post(new AfterAccessEvent(this, tokenCacheEvent));

        return items;
    }

    public void add(AuthenticationResult result, String authority, String resource) {
        TokenCacheKey tokenCacheKey = new TokenCacheKey(authority, resource, result.getUserInfo());
        eventBus.post(new BeforeWriteEvent(
                this, this, tokenCacheKey.getClientId(), resource,
                tokenCacheKey.getUniqueId(), tokenCacheKey.getDisplayableId()));
        tokenCacheMap.put(tokenCacheKey, result);
        hasStateChanged = true;
    }

    public void deleteItem(TokenCacheItem tokenCacheItem) {
        Preconditions.checkNotNull(tokenCacheItem, "tokenCacheItem");

        TokenCacheEvent tokenCacheEvent = new TokenCacheEvent(
                this, this,
                tokenCacheItem.getClientId(),
                tokenCacheItem.getResource(),
                tokenCacheItem.getUniqueId(),
                tokenCacheItem.getDisplayableId());

        eventBus.post(new BeforeAccessEvent(this, tokenCacheEvent));
        eventBus.post(new BeforeWriteEvent(this, tokenCacheEvent));

        TokenCacheKey key = new TokenCacheKey(
                tokenCacheItem.getAuthority(),
                tokenCacheItem.getResource(),
                tokenCacheItem.getAuthenticationResult().getUserInfo());
        if(tokenCacheMap.containsKey(key)) {
            tokenCacheMap.remove(key);
            hasStateChanged = true;
        }

        eventBus.post(new AfterAccessEvent(this, tokenCacheEvent));
    }

    public void clear() throws IOException {
        TokenCacheEvent tokenCacheEvent = new TokenCacheEvent(this);
        tokenCacheEvent.setTokenCache(this);
        eventBus.post(new BeforeAccessEvent(this, tokenCacheEvent));
        eventBus.post(new BeforeWriteEvent(this, tokenCacheEvent));
        tokenCacheMap.clear();
        hasStateChanged = true;
        eventBus.post(new AfterAccessEvent(this, tokenCacheEvent));
    }

    public boolean hasStateChanged() {
        return hasStateChanged;
    }

    public void setStateChanged(boolean hasStateChanged) {
        this.hasStateChanged = hasStateChanged;
    }

    public int getCount() {
        return tokenCacheMap.size();
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
