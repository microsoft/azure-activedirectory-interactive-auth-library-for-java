package com.microsoftopentechnologies.auth.cache;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.microsoftopentechnologies.auth.UserInfo;

public class TokenCacheKey {
    private String authority;
    private String clientId;
    private String displayableId;
    private String resource;
    private String uniqueId;

    /**
     * Initialize a {@link TokenCacheKey} object. The mapping below is taken from
     * MSDN: https://msdn.microsoft.com/en-us/library/azure/dn645542.aspx
     * @param authority The authority URL.
     * @param resource The resource for which this token was issued.
     * @param userInfo The {@link UserInfo} object containing the value from the
     *                 {@code id_token}.
     */
    public TokenCacheKey(String authority, String resource, UserInfo userInfo) {
        this(
                Strings.nullToEmpty(authority),
                Strings.nullToEmpty(userInfo.getClaims().getAudience()),
                Strings.nullToEmpty(userInfo.getUniqueName()),
                Strings.nullToEmpty(resource),
                Strings.nullToEmpty(userInfo.getClaims().getSubject())
        );
    }

    public TokenCacheKey(String authority, String clientId, String displayableId, String resource, String uniqueId) {
        this.authority = authority;
        this.clientId = clientId;
        this.displayableId = displayableId;
        this.resource = resource;
        this.uniqueId = uniqueId;
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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  TokenCacheKey))
            return false;

        return equals((TokenCacheKey)obj);
    }

    public boolean equals(TokenCacheKey tokenCacheKey) {
        if(this == tokenCacheKey)
            return true;

        return  authority.equals(tokenCacheKey.authority) &&
                resource.equals(tokenCacheKey.resource) &&
                clientId.equals(tokenCacheKey.clientId) &&
                displayableId.equals(tokenCacheKey.displayableId) &&
                uniqueId.equals(tokenCacheKey.uniqueId);
    }

    @Override
    public int hashCode() {
        return Joiner.on(":::").
                join(authority, resource, clientId, displayableId, uniqueId).hashCode();
    }
}
