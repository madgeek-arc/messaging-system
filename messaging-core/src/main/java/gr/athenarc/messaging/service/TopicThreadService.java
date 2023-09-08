package gr.athenarc.messaging.service;

import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.TopicThread;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TopicThreadService extends CrudOperations<TopicThread, String> {

    Mono<TopicThread> getByIdAndUserEmailOrGroup(String threadId, String email, String groupId);

    Flux<TopicThread> browse(Sort sort);

    Flux<TopicThread> browse(Example<TopicThread> example, Sort sort);

    Mono<TopicThread> addMessage(String threadId, Message message, boolean anonymousSender);

    Mono<TopicThread> readMessage(String threadId, String messageId, boolean read, String userId);

}
