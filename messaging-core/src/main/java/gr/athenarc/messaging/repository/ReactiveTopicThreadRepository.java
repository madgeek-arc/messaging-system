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

package gr.athenarc.messaging.repository;

import gr.athenarc.messaging.domain.TopicThread;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReactiveTopicThreadRepository {

    Flux<TopicThread> findAllByTagsContainingIgnoreCase(List<String> tags, Pageable pageable);

    Flux<TopicThread> findAllByTagsContainingIgnoreCaseAndSubjectContainingIgnoreCase(List<String> tags, String subject, Pageable pageable);

    Flux<TopicThread> findAllBySubjectContainingIgnoreCase(String subject, Pageable pageable);

    Mono<TopicThread> findByIdAndUserOrGroup(String threadId, String email, String groupId);

    Flux<TopicThread> findAllUsingQuery(String regex, Pageable pageable);

    Flux<TopicThread> searchInbox(String groupId, String regex, String email, Pageable pageable);

    Flux<TopicThread> searchOutbox(String groupId, String regex, String email, Pageable pageable);

    Flux<TopicThread> searchUnread(List<String> groups, String email, Pageable pageable);

    Flux<TopicThread> searchUser(String email);

    /**
     * Finds every thread in which the given user appears in any capacity: as thread or message
     * sender or recipient, as the sender recorded in a message's metadata, or merely as a read
     * receipt. Deliberately wider than {@link #searchUser(String)}, which does not look at
     * message metadata - erasure has to reach every last trace.
     *
     * @param emailRegex an anchored regular expression matching the user's email
     */
    Flux<TopicThread> findAllByCorrespondentEmail(String emailRegex);
}
