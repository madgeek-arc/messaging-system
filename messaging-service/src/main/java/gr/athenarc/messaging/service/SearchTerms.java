/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.athenarc.messaging.service;

import java.util.regex.Pattern;

/**
 * Converts user-supplied search text into a regular expression that matches the text literally.
 */
public final class SearchTerms {

    private SearchTerms() {
    }

    /**
     * @return {@code term} quoted for use in a {@code $regex} filter, or an empty string (matching
     * everything) when {@code term} is null or empty
     */
    public static String literal(String term) {
        return term == null || term.isEmpty() ? "" : Pattern.quote(term);
    }
}
