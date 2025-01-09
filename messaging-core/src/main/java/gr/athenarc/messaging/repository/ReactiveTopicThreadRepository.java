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
}
