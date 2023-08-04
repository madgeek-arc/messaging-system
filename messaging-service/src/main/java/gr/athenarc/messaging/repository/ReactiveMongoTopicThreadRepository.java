package gr.athenarc.messaging.repository;

import gr.athenarc.messaging.domain.TopicThread;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ReactiveMongoTopicThreadRepository extends ReactiveMongoRepository<TopicThread, String>, ReactiveTopicThreadRepository {

    @Override
    Flux<TopicThread> findAllByTagsContainingIgnoreCase(List<String> tags, Pageable pageable);

    @Override
    Flux<TopicThread> findAllByTagsContainingIgnoreCaseAndSubjectContainingIgnoreCase(List<String> tags, String subject, Pageable pageable);

    @Override
    Flux<TopicThread> findAllBySubjectContainingIgnoreCase(String subject, Pageable pageable);

    @Override
    @Query(value = "{'$or':[ {'subject': { '$regex': ?0, $options: 'i'}}, {'tags': { '$regex': ?0, $options: 'i'}}, {'messages.message.from.email': { '$regex': ?0, $options: 'i'}}, {'messages.message.to.email': { '$regex': ?0, $options: 'i'}}, {'messages.message.to.groupId': { '$regex': ?0, $options: 'i'}} ]}")
    Flux<Object> findAllUsingQuery(String regex, Pageable pageable);

    @Override
//    @Query(value = "{'$and': [ {'to.groupId': ?0}, {'$or':[ {'subject': { '$regex': ?1, $options: 'i'}}, {'tags': { '$regex': ?1, $options: 'i'}}, {'messages.message.from.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.groupId': { '$regex': ?1, $options: 'i'}} ]} ]}")
    @Query(value = "{'$and': [ {'messages.message.to.groupId': ?0}, {'$or':[ {'subject': { '$regex': ?1, $options: 'i'}}, {'tags': { '$regex': ?1, $options: 'i'}}, {'messages.message.from.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.groupId': { '$regex': ?0, $options: 'i'}} ]} ]}")
    Flux<TopicThread> searchInbox(String groupId, String regex, String email, Pageable pageable);

    @Override
//    @Query(value = "{'$and': [ {'$and':[ {'$or':[ {'from.groupId': ?0}, {'to.groupId': { '$regex': ?0, $options: 'i'}} ] }, {'messages.message.from.email': ?2}]}, {'$or':[ {'subject': { '$regex': ?1, $options: 'i'}}, {'tags': { '$regex': ?1, $options: 'i'}}, {'messages.message.from.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.groupId': { '$regex': ?1, $options: 'i'}} ]} ]}")
    @Query(value = "{'$and': [ {'messages.message.from.email': ?2}, {'$or':[ {'messages.message.from.groupId': ?0}, {'subject': { '$regex': ?1, $options: 'i'}}, {'tags': { '$regex': ?1, $options: 'i'}}, {'messages.message.from.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.email': { '$regex': ?1, $options: 'i'}}, {'messages.message.to.groupId': { '$regex': ?1, $options: 'i'}} ]} ]}")
    Flux<TopicThread> searchOutbox(String groupId, String regex, String email, Pageable pageable);

    @Override
    @Query(value = "{ '$and': [ {'messages.message.to.groupId': { '$in': ?0 }}, {'messages.message.readBy': {'$nin':  [?1] }} ] }")
    Flux<TopicThread> searchUnread(List<String> groups, String email, Pageable pageable);
}
