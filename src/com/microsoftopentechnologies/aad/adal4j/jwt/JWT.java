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

package com.microsoftopentechnologies.aad.adal4j.jwt;

import com.google.gson.JsonObject;
import com.microsoftopentechnologies.aad.adal4j.utils.JsonUtils;

import java.text.ParseException;

public class JWT {
    private String algorithm;
    private String type;
    private String contentType;
    private JWTClaimsSet jwtClaimsSet;

    public static JWT parse(JsonObject header, JsonObject claims) throws ParseException {
        JWT jwt = new JWT();
        jwt.setType(JsonUtils.getJsonStringProp(header, "typ"));
        jwt.setAlgorithm(JsonUtils.getJsonStringProp(header, "alg"));
        jwt.setContentType(JsonUtils.getJsonStringProp(header, "cty"));

        // we only support JWTs where the algorithm is set to "none"
        if(!jwt.getAlgorithm().equals("none")) {
            throw new ParseException("This JWT parser only supports plain text JWTs. Found algorithm: " +
                jwt.getAlgorithm(), 0);
        }

        jwt.setJwtClaimsSet(JWTClaimsSet.parse(claims));

        return jwt;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JWTClaimsSet getJWTClaimsSet() {
        return jwtClaimsSet;
    }

    public void setJwtClaimsSet(JWTClaimsSet jwtClaimsSet) {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
