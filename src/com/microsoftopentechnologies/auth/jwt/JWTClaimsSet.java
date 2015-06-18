/**
 * Copyright 2014 Microsoft Open Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.microsoftopentechnologies.auth.jwt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoftopentechnologies.auth.utils.JsonUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTClaimsSet {
    private String issuer;
    private String subject;
    private String audience;
    private Date expirationTime;
    private Date notBeforeTime;
    private Date issueTime;
    private String jwtID;
    private Map<String, JsonElement> customClaims = new HashMap<String, JsonElement>();

    private static ImmutableList<String> standardClaims = ImmutableList.of(
            "iss", "sub", "aud", "exp", "nbf", "iat", "jti");

    public static JWTClaimsSet parse(JsonObject claims) {
        JWTClaimsSet claimsSet = new JWTClaimsSet();

        // initialize standard claims
        claimsSet.setIssuer(JsonUtils.getJsonStringProp(claims, "iss"));
        claimsSet.setSubject(JsonUtils.getJsonStringProp(claims, "sub"));
        claimsSet.setAudience(JsonUtils.getJsonStringProp(claims, "aud"));
        claimsSet.setExpirationTime(new Date(JsonUtils.getJsonLongProp(claims, "exp") * 1000));
        claimsSet.setNotBeforeTime(new Date(JsonUtils.getJsonLongProp(claims, "nbf") * 1000));
        claimsSet.setIssueTime(new Date(JsonUtils.getJsonLongProp(claims, "iat") * 1000));
        claimsSet.setJwtID(JsonUtils.getJsonStringProp(claims, "jti"));

        // initialize custom claims
        Map<String, JsonElement> customClaims = claimsSet.getCustomClaims();
        for(Map.Entry<String, JsonElement> entry : claims.entrySet()) {
            if(!Iterables.contains(standardClaims, entry.getKey())) {
                customClaims.put(entry.getKey(), entry.getValue());
            }
        }

        return claimsSet;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getNotBeforeTime() {
        return notBeforeTime;
    }

    public void setNotBeforeTime(Date notBeforeTime) {
        this.notBeforeTime = notBeforeTime;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public String getJwtID() {
        return jwtID;
    }

    public void setJwtID(String jwtID) {
        this.jwtID = jwtID;
    }

    public Map<String, JsonElement> getCustomClaims() {
        return customClaims;
    }
}
