package gr.athenarc.messaging.dao;

import gr.athenarc.messaging.domain.Thread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadRepository extends MongoRepository<Thread, String> {
}
