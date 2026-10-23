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
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import gr.athenarc.messaging.service.TopicThreadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MessagingController.class)
@Import(SecurityConfig.class)
class PublicModeSecurityTest {

    @Autowired
    WebTestClient client;

    @MockitoBean
    TopicThreadService topicThreadService;

    @MockitoBean
    ReactiveMongoTopicThreadRepository repository;

    @Test
    void requestsPassWithoutKeyWhenNoneIsConfigured() {
        when(repository.countInbox(anyString(), anyString(), anyString())).thenReturn(Mono.just(1L));

        client.get().uri("/inbox/threads/count?groupId=g&email=e")
                .exchange()
                .expectStatus().isOk();
    }
}
