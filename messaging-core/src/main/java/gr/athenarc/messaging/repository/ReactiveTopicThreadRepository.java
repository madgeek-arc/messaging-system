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
