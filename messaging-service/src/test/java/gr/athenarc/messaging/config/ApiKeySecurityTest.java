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

import gr.athenarc.messaging.controller.MessagingController;
import gr.athenarc.messaging.controller.MessagingHeaders;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import gr.athenarc.messaging.service.TopicThreadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MessagingController.class, properties = "messaging-system.api-key=secret")
@Import(SecurityConfig.class)
class ApiKeySecurityTest {

    @Autowired
    WebTestClient client;

    @MockitoBean
    TopicThreadService topicThreadService;

    @MockitoBean
    ReactiveMongoTopicThreadRepository repository;

    @BeforeEach
    void stubs() {
        when(repository.countInbox(anyString(), anyString(), anyString())).thenReturn(Mono.just(3L));
        when(topicThreadService.readMessage(anyString(), anyString(), anyBoolean(), any()))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Test
    void requestWithoutKeyIsUnauthorized() {
        client.get().uri("/inbox/threads/count?groupId=g&email=e")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithWrongKeyIsUnauthorized() {
        client.get().uri("/inbox/threads/count?groupId=g&email=e")
                .header(MessagingHeaders.API_KEY, "wrong")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void requestWithKeyReachesTheController() {
        client.get().uri("/inbox/threads/count?groupId=g&email=e")
                .header(MessagingHeaders.API_KEY, "secret")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class).isEqualTo(3);
    }

    @Test
    void healthPathIsExempt() {
        client.get().uri("/actuator/health/liveness")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void serviceErrorsKeepTheirStatus() {
        client.patch().uri("/threads/t/messages/0?read=true&userId=u")
                .header(MessagingHeaders.API_KEY, "secret")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void anonymizeWithoutEmailIsBadRequest() {
        client.post().uri("/user/anonymize")
                .header(MessagingHeaders.API_KEY, "secret")
                .header("Content-Type", "application/json")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
