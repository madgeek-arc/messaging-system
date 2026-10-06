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

import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.privacy.PersonalDataEraser;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultTopicThreadService implements TopicThreadService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTopicThreadService.class);
    private final ReactiveMongoTopicThreadRepository topicThreadRepository;

    public DefaultTopicThreadService(ReactiveMongoTopicThreadRepository topicThreadRepository) {
        this.topicThreadRepository = topicThreadRepository;
    }

    @Override
    public Mono<TopicThread> get(String id) {
        return topicThreadRepository.findById(id);
    }

    @Override
    public Mono<TopicThread> add(TopicThread topicThread) {
        Date now = new Date();
        topicThread.setCreated(now);
        topicThread.setUpdated(now);
        if (topicThread.getMessages() != null) {
            for (int i = 0; i < topicThread.getMessages().size(); i++) {
                topicThread.getMessages().get(i).setId(String.valueOf(i));
                topicThread.getMessages().get(i).getMessage().setDate(now);
                topicThread.getMessages().get(i).getMetadata().getReadBy().add(topicThread.getFrom().getEmail());
            }
            logger.warn("Adding new Thread with {} messages", topicThread.getMessages().size());
        }
        return topicThreadRepository.save(topicThread);
    }

    @Override
    public Mono<TopicThread> update(String id, TopicThread topicThread) {
        return topicThreadRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Not Found")))
                .flatMap(s -> topicThreadRepository.save(topicThread));
    }

    @Override
    public Mono<Void> delete(String id) {
        return topicThreadRepository.deleteById(id);
    }

    @Override
    public Mono<TopicThread> getByIdAndUserEmailOrGroup(String threadId, String email, String groupId) {
        return topicThreadRepository.findByIdAndUserOrGroup(threadId, email, groupId);
    }

    @Override
    public Flux<TopicThread> browse(Sort sort) {
        return topicThreadRepository.findAll(sort);
    }

    @Override
    public Flux<TopicThread> browse(Example<TopicThread> example, Sort sort) {
        return topicThreadRepository.findAll(example, sort);
    }

    @Override
    public synchronized Mono<TopicThread> addMessage(String threadId, Message message, boolean anonymousSender) {
        return topicThreadRepository.findById(threadId).flatMap(
                        topic -> {
                            StoredMessage storedMessage = StoredMessage.of(message, anonymousSender);
                            storedMessage.setId(String.valueOf(topic.getMessages().size()));
                            storedMessage.getMetadata().getReadBy().add(message.getFrom().getEmail());
                            topic.getMessages().add(storedMessage);
                            return topicThreadRepository.save(topic);
                        })
                .log();
    }

    @Override
    public synchronized Mono<TopicThread> readMessage(String threadId, String messageId, boolean read, String userId) {
        return get(threadId).flatMap(thread -> {
            StoredMessage message = thread.getMessages().get(Integer.parseInt(messageId));
            if (read) {
                message.getMetadata().getReadBy().add(userId);
            } else {
                message.getMetadata().getReadBy().remove(userId);
            }
            return this.topicThreadRepository.save(thread);
        });
    }

    @Override
    public Mono<Page<ThreadDTO>> getInbox(String groupId, String regex, String email, Pageable pageable) {
        return topicThreadRepository.searchInbox(groupId, regex, email, pageable)
                .filter(Objects::nonNull)
                .map(topic -> new ThreadDTO(topic, email))
                .collectList()
                .zipWith(topicThreadRepository.countInbox(groupId, regex, email))
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    /**
     * Threads whose personal data is already erased are filtered out before the save, so a repeat
     * call is a no-op that reports 0. The 'updated' timestamp is deliberately not touched: every
     * inbox query sorts by it, and bumping it would float a purged user's oldest threads back to
     * the top of everyone else's inbox.
     */
    @Override
    public Mono<Integer> anonymizeUser(String email) {
        // Anchored and quoted so that the '.' and '+' that are commonplace in email addresses are
        // matched literally rather than as regex metacharacters.
        String emailRegex = "^" + Pattern.quote(email) + "$";
        return topicThreadRepository.findAllByCorrespondentEmail(emailRegex)
                .filter(thread -> PersonalDataEraser.anonymize(thread, email))
                .flatMap(topicThreadRepository::save)
                .count()
                .map(Long::intValue)
                // Deliberately omits the email: logging the identifier being erased would itself
                // retain the personal data this method exists to remove.
                .doOnSuccess(count -> logger.info("Erased personal data from {} thread(s).", count));
    }
}
