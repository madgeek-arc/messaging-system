package gr.athenarc.messaging.repository;

import gr.athenarc.messaging.domain.TopicThread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReactiveTopicThreadRepository extends ReactiveSortingRepository<TopicThread, String> {

    Flux<TopicThread> findAllByTagsContainingIgnoreCase(List<String> tags, Pageable pageable);

    Flux<TopicThread> findAllBySubjectContainingIgnoreCase(String subject, Pageable pageable);

    Flux<Object> findAllUsingQuery(String regex, Pageable pageable);

    Flux<TopicThread> searchInbox(String groupId, String regex, Pageable pageable);

    Flux<TopicThread> searchOutbox(String groupId, String regex, String email, Pageable pageable);

    Flux<TopicThread> searchUnread(List<String> groups, Pageable pageable);
}
