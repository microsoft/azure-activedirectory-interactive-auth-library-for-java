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

package com.microsoftopentechnologies.auth.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static String getJsonStringProp(JsonObject obj, String propName) {
        JsonElement element = obj.get(propName);
        if (element != null) {
            return element.getAsString();
        }

        return "";
    }

    public static long getJsonLongProp(JsonObject obj, String propName) {
        JsonElement element = obj.get(propName);
        if (element != null) {
            return element.getAsLong();
        }

        return Long.MIN_VALUE;
    }

    public static long getAsLong(JsonElement element) {
        if(element != null) {
            return element.getAsLong();
        }

        return Long.MIN_VALUE;
    }

    public static String getAsString(JsonElement element) {
        if(element != null) {
            return element.getAsString();
        }

        return "";
    }
}
