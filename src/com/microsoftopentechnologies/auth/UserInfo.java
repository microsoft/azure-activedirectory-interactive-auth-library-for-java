/**
 * Copyright 2014 Microsoft Open Technologies Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoftopentechnologies.auth;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.microsoftopentechnologies.auth.jwt.JWT;
import com.microsoftopentechnologies.auth.jwt.JWTClaimsSet;
import com.microsoftopentechnologies.auth.jwt.JWTParser;
import com.microsoftopentechnologies.auth.utils.JsonUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

/**
 * Contains information of a single user.
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String userId;
    private final String givenName;
    private final String familyName;
    private final String identityProvider;
    private final String upn;
    private final String uniqueName;
    private final String tenantId;

    private UserInfo(final String userId,
                     final String givenName,
                     final String familyName,
                     final String identityProvider,
                     final String upn,
                     final String uniqueName,
                     final String tenantId) {
        this.userId = userId;
        this.givenName = givenName;
        this.familyName = familyName;
        this.identityProvider = identityProvider;
        this.upn = upn;
        this.uniqueName = uniqueName;
        this.tenantId = tenantId;
    }

    public static UserInfo parse(String idtoken) throws ParseException {
        if (Strings.isNullOrEmpty(idtoken)) {
            return null;
        }

        JWT jwt = JWTParser.parse(idtoken);
        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        Map<String, JsonElement> customClaims = claims.getCustomClaims();

        return new UserInfo(
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.ObjectId)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.GivenName)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.FamilyName)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.IdentityProvider)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.UPN)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.UniqueName)),
                JsonUtils.getAsString(customClaims.get(IdTokenClaim.TenantId)));
    }

    /**
     * Get user id
     *
     * @return String value
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Get given name
     *
     * @return String value
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Get family name
     *
     * @return String value
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Get identity provider
     *
     * @return String value
     */
    public String getIdentityProvider() {
        return identityProvider;
    }

    /**
     * Get user principal name
     *
     * @return String value
     */
    public String getUpn() {
        return upn;
    }

    /**
     * Get unique name
     *
     * @return String value
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * Get tenant id
     *
     * @return String value
     */
    public String getTenantId() {
        return tenantId;
    }
}