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

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class SearchTermsTest {

    @Test
    void emptyOrNullTermMatchesEverything() {
        assertThat(SearchTerms.literal(null)).isEmpty();
        assertThat(SearchTerms.literal("")).isEmpty();
    }

    @Test
    void metacharactersMatchLiterally() {
        Pattern pattern = Pattern.compile(SearchTerms.literal("a.b+(c"));

        assertThat(pattern.matcher("x a.b+(c y").find()).isTrue();
        assertThat(pattern.matcher("axbbb(c").find()).isFalse();
    }

    @Test
    void quoteTerminatorInTermIsEscaped() {
        Pattern pattern = Pattern.compile(SearchTerms.literal("x\\E.*"));

        assertThat(pattern.matcher("x\\E.*").find()).isTrue();
        assertThat(pattern.matcher("x\\Eanything").find()).isFalse();
    }
}
