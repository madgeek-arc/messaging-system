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
import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Builds the {@link WebClient} that calls the messaging service.
 */
public final class MessagingWebClients {

    private MessagingWebClients() {
    }

    /**
     * Keeps the filters and default headers of {@code builder} and replaces its connector with one
     * applying the configured connect and response timeouts.
     *
     * @return a client for {@code messaging-system.client.endpoint} that sends
     * {@code messaging-system.client.api-key} in the {@value MessagingHeaders#API_KEY} header when set
     */
    public static WebClient create(MessagingClientProperties properties, WebClient.Builder builder) {
        MessagingClientProperties.Client client = properties.getClient();
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(client.getConnectTimeout().toMillis()))
                .responseTimeout(client.getResponseTimeout());
        WebClient.Builder configured = builder
                .baseUrl(client.getEndpoint())
                .clientConnector(new ReactorClientHttpConnector(httpClient));
        if (StringUtils.hasText(client.getApiKey())) {
            configured.defaultHeader(MessagingHeaders.API_KEY, client.getApiKey());
        }
        return configured.build();
    }
}
