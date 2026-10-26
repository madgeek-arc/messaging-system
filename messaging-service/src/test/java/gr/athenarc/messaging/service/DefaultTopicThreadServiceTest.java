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

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
@Import(DefaultTopicThreadService.class)
class DefaultTopicThreadServiceTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @Autowired
    DefaultTopicThreadService service;

    @Autowired
    ReactiveMongoTopicThreadRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll().block();
    }

    @Test
    void concurrentAppendsKeepEveryMessageWithUniqueIds() {
        TopicThread thread = service.add(newThread("Subject", 1)).block();

        Flux.range(0, 20)
                .flatMap(i -> service.addMessage(thread.getId(), message("user" + i + "@example.org"), false)
                        .subscribeOn(Schedulers.parallel()))
                .blockLast();

        TopicThread stored = repository.findById(thread.getId()).block();
        assertThat(stored.getMessages()).hasSize(21);
        assertThat(stored.getMessages().stream().map(StoredMessage::getId).collect(Collectors.toSet()))
                .isEqualTo(IntStream.range(0, 21).mapToObj(String::valueOf).collect(Collectors.toSet()));
    }

    @Test
    void appendToThreadWithoutMessagesGetsIdZero() {
        TopicThread thread = newThread("Empty", 0);
        thread.setMessages(null);
        thread = service.add(thread).block();

        TopicThread updated = service.addMessage(thread.getId(), message("a@example.org"), true).block();

        assertThat(updated.getMessages()).singleElement().satisfies(m -> {
            assertThat(m.getId()).isEqualTo("0");
            assertThat(m.getMetadata().isAnonymousSender()).isTrue();
            assertThat(m.getMetadata().getReadBy()).containsExactly("a@example.org");
        });
    }

    @Test
    void appendToMissingThreadIsNotFound() {
        assertStatus(() -> service.addMessage("missing", message("a@example.org"), false).block(), HttpStatus.NOT_FOUND);
    }

    @Test
    void appendWithoutSenderIsBadRequest() {
        TopicThread thread = service.add(newThread("Subject", 1)).block();
        Message message = message("a@example.org");
        message.setFrom(null);

        assertStatus(() -> service.addMessage(thread.getId(), message, false).block(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void readReceiptsAreAddedAndRemoved() {
        TopicThread thread = service.add(newThread("Subject", 2)).block();

        service.readMessage(thread.getId(), "1", true, "reader@example.org").block();
        assertThat(readBy(thread.getId(), 1)).contains("reader@example.org");
        assertThat(readBy(thread.getId(), 0)).doesNotContain("reader@example.org");

        service.readMessage(thread.getId(), "1", false, "reader@example.org").block();
        assertThat(readBy(thread.getId(), 1)).doesNotContain("reader@example.org");
    }

    @Test
    void concurrentReadReceiptsAreAllKept() {
        TopicThread thread = service.add(newThread("Subject", 1)).block();

        Flux.range(0, 20)
                .flatMap(i -> service.readMessage(thread.getId(), "0", true, "reader" + i + "@example.org")
                        .subscribeOn(Schedulers.parallel()))
                .blockLast();

        assertThat(readBy(thread.getId(), 0)).hasSize(21);
    }

    @Test
    void invalidReadRequestsAreRejected() {
        TopicThread thread = service.add(newThread("Subject", 1)).block();

        assertStatus(() -> service.readMessage(thread.getId(), "x", true, "r@example.org").block(), HttpStatus.BAD_REQUEST);
        assertStatus(() -> service.readMessage(thread.getId(), "-1", true, "r@example.org").block(), HttpStatus.BAD_REQUEST);
        assertStatus(() -> service.readMessage(thread.getId(), "0", true, " ").block(), HttpStatus.BAD_REQUEST);
        assertStatus(() -> service.readMessage(thread.getId(), "5", true, "r@example.org").block(), HttpStatus.NOT_FOUND);
        assertStatus(() -> service.readMessage("missing", "0", true, "r@example.org").block(), HttpStatus.NOT_FOUND);
    }

    @Test
    void updateKeepsThePathIdAndLeavesOtherThreadsAlone() {
        TopicThread first = service.add(newThread("First", 1)).block();
        TopicThread second = service.add(newThread("Second", 1)).block();

        TopicThread body = newThread("Renamed", 1);
        body.setId(second.getId());
        service.update(first.getId(), body).block();

        assertThat(repository.findById(first.getId()).block().getSubject()).isEqualTo("Renamed");
        assertThat(repository.findById(second.getId()).block().getSubject()).isEqualTo("Second");
        assertThat(repository.count().block()).isEqualTo(2);
    }

    @Test
    void updateOfMissingThreadIsNotFound() {
        assertStatus(() -> service.update("missing", newThread("X", 0)).block(), HttpStatus.NOT_FOUND);
    }

    @Test
    void inboxSearchMatchesTermsLiterally() {
        service.add(newThread("Report (draft) v1.2", 1)).block();
        service.add(newThread("Report draft v1x2", 1)).block();

        assertThat(inboxSubjects("(draft)")).containsExactly("Report (draft) v1.2");
        assertThat(inboxSubjects("v1.2")).containsExactly("Report (draft) v1.2");
        assertThat(inboxSubjects("")).hasSize(2);
    }

    private List<String> inboxSubjects(String term) {
        return service.getInbox("group", term, "", PageRequest.of(0, 10)).block()
                .map(ThreadDTO::getSubject).getContent();
    }

    private List<String> readBy(String threadId, int index) {
        return new ArrayList<>(repository.findById(threadId).block().getMessages().get(index).getMetadata().getReadBy());
    }

    private static void assertStatus(Runnable call, HttpStatus status) {
        assertThatThrownBy(call::run)
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> assertThat(e.getStatusCode()).isEqualTo(status));
    }

    private static TopicThread newThread(String subject, int messages) {
        TopicThread thread = new TopicThread();
        thread.setSubject(subject);
        thread.setFrom(new Correspondent("Sender", "sender@example.org", "senders"));
        thread.setTo(List.of(new Correspondent("Group", null, "group")));
        thread.setMessages(new ArrayList<>());
        for (int i = 0; i < messages; i++) {
            thread.getMessages().add(StoredMessage.of(message("sender@example.org"), false));
        }
        return thread;
    }

    private static Message message(String from) {
        return new Message(new Correspondent("User", from, "senders"),
                List.of(new Correspondent("Group", null, "group")), "Body", null, null);
    }
}
