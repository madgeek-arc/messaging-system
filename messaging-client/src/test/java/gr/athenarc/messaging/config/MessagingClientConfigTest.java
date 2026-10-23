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

import com.sun.net.httpserver.HttpServer;
import gr.athenarc.messaging.controller.MessagingHeaders;
import gr.athenarc.messaging.service.MessagingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessagingClientConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, CodecsAutoConfiguration.class,
                    WebClientAutoConfiguration.class, MessagingClientConfig.class));

    private final Map<String, String> received = new ConcurrentHashMap<>();
    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/inbox/threads/count", exchange -> {
            exchange.getRequestHeaders().forEach((name, values) -> received.put(name.toLowerCase(), values.get(0)));
            byte[] body = "5".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.createContext("/api/inbox/unread", exchange -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private String endpoint() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/api/";
    }

    @Test
    void sendsApiKeyAndAppliesApplicationCustomizers() {
        runner.withPropertyValues("messaging-system.client.endpoint=" + endpoint(),
                        "messaging-system.client.api-key=secret")
                .withBean(WebClientCustomizer.class, () -> builder -> builder.defaultHeader("Authorization", "Bearer token"))
                .run(context -> {
                    Integer count = context.getBean(MessagingService.class).countInbox("g", "", "e").block();

                    assertThat(count).isEqualTo(5);
                    assertThat(received).containsEntry(MessagingHeaders.API_KEY.toLowerCase(), "secret")
                            .containsEntry("authorization", "Bearer token");
                });
    }

    @Test
    void sendsNoApiKeyWhenNoneIsConfigured() {
        runner.withPropertyValues("messaging-system.client.endpoint=" + endpoint())
                .run(context -> {
                    context.getBean(MessagingService.class).countInbox("g", "", "e").block();

                    assertThat(received).doesNotContainKey(MessagingHeaders.API_KEY.toLowerCase());
                });
    }

    @Test
    void responseTimeoutApplies() {
        runner.withPropertyValues("messaging-system.client.endpoint=" + endpoint(),
                        "messaging-system.client.response-timeout=200ms")
                .run(context -> {
                    MessagingService service = context.getBean(MessagingService.class);
                    long start = System.nanoTime();

                    assertThatThrownBy(() -> service.searchUnreadThreads(List.of("g"), "e").block());
                    assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofMillis(1500));
                });
    }

    @Test
    void worksWithoutAnApplicationWebClientBuilder() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MessagingClientConfig.class))
                .withPropertyValues("messaging-system.client.endpoint=" + endpoint())
                .run(context -> assertThat(context.getBean(MessagingService.class).countInbox("g", "", "e").block())
                        .isEqualTo(5));
    }

    @Test
    void backsOffWhenTheApplicationDefinesItsOwnClient() {
        MessagingService own = new MessagingService(new MessagingClientProperties());
        runner.withBean(MessagingService.class, () -> own)
                .run(context -> assertThat(context.getBean(MessagingService.class)).isSameAs(own));
    }
}
