package gr.athenarc.messaging.repository;

import gr.athenarc.messaging.domain.TopicThread;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface TopicThreadRepository extends ReactiveMongoRepository<TopicThread, String> {

    Flux<TopicThread> findAllByTagsContainingIgnoreCase(List<String> tags, Sort sort);
}
