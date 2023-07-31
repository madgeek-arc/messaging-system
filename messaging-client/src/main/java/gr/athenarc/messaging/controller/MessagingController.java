package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.config.MessagingClientProperties;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadMessages;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class MessagingController implements TopicThreadsController {

    private final WebClient webClient;

    public MessagingController(MessagingClientProperties messagingClientProperties) {
        this.webClient = WebClient.builder().baseUrl(messagingClientProperties.getClient().getEndpoint()).build();
    }

    @Override
    public Mono<ThreadDTO> get(String threadId) {
        return this.webClient.get()
                .uri(RestApiPaths.THREADS_id, threadId)
                .exchangeToMono(body -> body.bodyToMono(ThreadDTO.class));
    }

    @Override
    public Mono<ThreadDTO> add(ThreadDTO thread) {
        return null;
    }

    @Override
    public Mono<ThreadDTO> update(String threadId, TopicThread topicThread) {
        return null;
    }

    @Override
    public Mono<Void> delete(String threadId) {
        return null;
    }

    @Override
    public Mono<UnreadMessages> searchTotalUnread(List<String> groups) {
        return null;
    }

    @Override // FIXME
    public Flux<ThreadDTO> searchInbox(String groupId, String regex, String sortBy, Sort.Direction direction, Integer page, Integer size, Authentication authentication) {
        return null;
    }

    @Override
    public Flux<ThreadDTO> searchInboxUnread(List<String> groups, String sortBy, Sort.Direction direction, Integer page, Integer size, Authentication authentication) {
        return null;
    }

    @Override // FIXME
    public Flux<ThreadDTO> searchOutbox(String groupId, String email, String regex, String sortBy, Sort.Direction direction, Integer page, Integer size, Authentication authentication) {
        return null;
    }

    @Override // FIXME
    public Mono<ThreadDTO> addExternal(String recaptcha, ThreadDTO thread) {
        return null;
    }

    @Override // FIXME
    public Mono<ThreadDTO> addMessage(String threadId, Message message, boolean anonymous, Authentication authentication) {
        return null;
    }

    @Override // FIXME
    public Mono<ThreadDTO> readMessage(String threadId, String messageId, boolean read) {
        return null;
    }

}
