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
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadThreads;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ThreadOperations {

    Mono<ThreadDTO> get(
            String threadId,
            String email,
            String groupId);

    Mono<ThreadDTO> add(
            ThreadDTO thread);

    Mono<ThreadDTO> update(
            String threadId,
            TopicThread topicThread);

    Mono<Void> delete(
            String threadId);

    Mono<UnreadThreads> searchUnreadThreads(
            List<String> groups,
            String email);

    Mono<Integer> countInbox(
            String groupId,
            String regex,
            String email);

    Flux<ThreadDTO> searchInbox(
            String groupId,
            String regex,
            String email,
            String sortBy,
            Sort.Direction direction,
            Integer page,
            Integer size);

    Flux<ThreadDTO> searchInboxUnread(
            List<String> groups,
            String email,
            String sortBy,
            Sort.Direction direction,
            Integer page,
            Integer size);

    Mono<Integer> countOutbox(
            String groupId,
            String regex,
            String email);

    Flux<ThreadDTO> searchOutbox(
            String groupId,
            String regex,
            String email,
            String sortBy,
            Sort.Direction direction,
            Integer page,
            Integer size);

    Mono<ThreadDTO> addMessage(
            String threadId,
            Message message,
            boolean anonymous);

    Mono<ThreadDTO> readMessage(
            String threadId,
            String messageId,
            boolean read,
            String userId);

    /**
     * Erases the given user's personal data from every thread they appear in, replacing their name
     * and email with a placeholder and dropping their read receipts. Message bodies and subjects
     * are left intact. Idempotent: a repeat call on an already erased user reports 0.
     *
     * @param email the email of the user to erase
     * @return the number of threads modified
     */
    Mono<Integer> anonymizeUser(
            String email);

}
