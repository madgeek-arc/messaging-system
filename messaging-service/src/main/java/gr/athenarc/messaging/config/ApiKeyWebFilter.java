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
package gr.athenarc.messaging.config;

import gr.athenarc.messaging.controller.MessagingHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Rejects with 401 every request that doesn't carry the configured key in the
 * {@value MessagingHeaders#API_KEY} header, except those matched by {@code exempt}.
 */
public class ApiKeyWebFilter implements WebFilter {

    private final byte[] apiKey;
    private final ServerWebExchangeMatcher exempt;

    public ApiKeyWebFilter(String apiKey, ServerWebExchangeMatcher exempt) {
        this.apiKey = apiKey.getBytes(StandardCharsets.UTF_8);
        this.exempt = exempt;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exempt.matches(exchange).flatMap(match -> {
            if (match.isMatch() || hasValidKey(exchange)) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        });
    }

    private boolean hasValidKey(ServerWebExchange exchange) {
        String provided = exchange.getRequest().getHeaders().getFirst(MessagingHeaders.API_KEY);
        return provided != null && MessageDigest.isEqual(apiKey, provided.getBytes(StandardCharsets.UTF_8));
    }
}
