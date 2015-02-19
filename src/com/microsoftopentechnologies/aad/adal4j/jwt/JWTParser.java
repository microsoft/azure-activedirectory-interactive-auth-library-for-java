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

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoftopentechnologies.aad.adal4j.utils.JsonUtils;

import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.util.List;

public class JWTParser {
    /**
     * Absolutely basic, bare minimum implementation of JWT parsing as needed
     * for parsing the ID token returned by Azure Active Directory as
     * documented here - https://msdn.microsoft.com/en-us/library/azure/dn645542.aspx.
     *
     * @param jwtInput The ID token JWT string.
     * @return The JWT object representing the information encoded in the JWT.
     * @throws ParseException
     */
    public static JWT parse(String jwtInput) throws ParseException {
        if(Strings.isNullOrEmpty(jwtInput)) {
            throw new ParseException("jwt string is null/empty.", 0);
        }

        // split the period delimited string
        List<String> tokens = Splitter.on('.').omitEmptyStrings().splitToList(jwtInput);

        // the length of the list MUST be 2 or more
        if(tokens.size() < 2) {
            throw new ParseException("Invalid JWT string supplied.", 0);
        }

        // parse JWT header and claims
        JsonParser jsonParser = new JsonParser();
        JsonObject header = (JsonObject)jsonParser.parse(stringFromBase64(tokens.get(0)));
        JsonObject claims = (JsonObject)jsonParser.parse(stringFromBase64(tokens.get(1)));

        return JWT.parse(header, claims);
    }

    private static String stringFromBase64(String str) {
        return new String(
                DatatypeConverter.parseBase64Binary(
                        normalizeB64String(str)));
    }

    /**
     * DatatypeConverter.parseBase64Binary has problems with base 64 encoded
     * strings whose length is not a multiple of 4. We fix this up by appending
     * '=' characters. Solution taken from: http://stackoverflow.com/a/9080594/8080.
     * @param s Input base 64 string.
     * @return Normalized so that length is a multiple of 4.
     */
    private static String normalizeB64String(String s) {
        while(s.length() % 4 != 0)
            s += "=";
        return s;
    }
}
