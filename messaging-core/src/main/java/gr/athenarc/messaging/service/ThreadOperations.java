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

}
