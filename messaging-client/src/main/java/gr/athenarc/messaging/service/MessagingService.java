package gr.athenarc.messaging.service;

import gr.athenarc.messaging.config.MessagingClientProperties;
import gr.athenarc.messaging.controller.RestApiPaths;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.AnonymizeUserRequest;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadThreads;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Calls the messaging service over HTTP.
 * <p>
 * Every method uses {@code retrieve()} rather than {@code exchangeToMono}/{@code exchangeToFlux},
 * so a 4xx/5xx response raises {@link org.springframework.web.reactive.function.client.WebClientResponseException}
 * carrying the status and body. Decoding an error response into the success type instead would
 * either fail with an opaque decoding error, yield a hollow object when the error body happens to
 * deserialize, or complete empty and surface as a NullPointerException in the caller.
 */
public class MessagingService implements ThreadOperations {

    private final WebClient webClient;

    public MessagingService(MessagingClientProperties messagingClientProperties) {
        this.webClient = WebClient.builder().baseUrl(messagingClientProperties.getClient().getEndpoint()).build();
    }

    @Override
    public Mono<ThreadDTO> get(String threadId, String email, String groupId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(RestApiPaths.THREADS_id)
                        .queryParam("email", email)
                        .queryParam("groupId", groupId)
                        .build(threadId))
                .retrieve()
                .bodyToMono(ThreadDTO.class);
    }

    @Override
    public Mono<ThreadDTO> add(ThreadDTO thread) {
        return this.webClient.post()
                .uri(RestApiPaths.THREADS)
                .body(BodyInserters.fromValue(thread))
                .retrieve()
                .bodyToMono(ThreadDTO.class);
    }

    @Override
    public Mono<ThreadDTO> update(String threadId, TopicThread topicThread) {
        return this.webClient.put()
                .uri(RestApiPaths.THREADS_id, threadId)
                .body(BodyInserters.fromValue(topicThread))
                .retrieve()
                .bodyToMono(ThreadDTO.class);
    }

    @Override
    public Mono<Void> delete(String threadId) {
        return this.webClient.delete()
                .uri(RestApiPaths.THREADS_id, threadId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<UnreadThreads> searchUnreadThreads(List<String> groups, String email) {
        return getUnreadThreads(groups, email);
    }

    @Override
    public Mono<Integer> countInbox(String groupId, String regex, String email) {
        return this.webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.INBOX_THREADS_COUNT)
                                .queryParam("groupId", groupId)
                                .queryParam("regex", regex)
                                .queryParam("email", email)
                                .build())
                .retrieve()
                .bodyToMono(Integer.class);
    }

    @Override
    public Flux<ThreadDTO> searchInbox(String groupId, String regex, String email, String sortBy, Sort.Direction direction, Integer page, Integer size) {
        return this.webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.INBOX_THREADS_SEARCH)
                                .queryParam("groupId", groupId)
                                .queryParam("regex", regex)
                                .queryParam("email", email)
                                .queryParam("sortBy", sortBy)
                                .queryParam("direction", direction)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .build())
                .retrieve()
                .bodyToFlux(ThreadDTO.class);
    }

    @Override
    public Flux<ThreadDTO> searchInboxUnread(List<String> groups, String email, String sortBy, Sort.Direction direction, Integer page, Integer size/*, Authentication authentication*/) {
        return this.webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.INBOX_THREADS_UNREAD)
                                .queryParam("groups", groups)
                                .queryParam("email", email)
                                .queryParam("sortBy", sortBy)
                                .queryParam("direction", direction)
                                .queryParam("page", page)
                                .queryParam("size", size)
//                                .queryParam("authentication", authentication)
                                .build())
                .retrieve()
                .bodyToFlux(ThreadDTO.class);
    }

    @Override
    public Mono<Integer> countOutbox(String groupId, String regex, String email) {
        return this.webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.OUTBOX_THREADS_COUNT)
                                .queryParam("groupId", groupId)
                                .queryParam("regex", regex)
                                .queryParam("email", email)
                                .build())
                .retrieve()
                .bodyToMono(Integer.class);
    }

    @Override
    public Flux<ThreadDTO> searchOutbox(String groupId, String regex, String email, String sortBy, Sort.Direction direction, Integer page, Integer size/*, Authentication authentication*/) {
        return this.webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.OUTBOX_THREADS_SEARCH)
                                .queryParam("groupId", groupId)
                                .queryParam("regex", regex)
                                .queryParam("email", email)
                                .queryParam("sortBy", sortBy)
                                .queryParam("direction", direction)
                                .queryParam("page", page)
                                .queryParam("size", size)
//                                .queryParam("authentication", authentication)
                                .build())
                .retrieve()
                .bodyToFlux(ThreadDTO.class);
    }

    @Override
    public Mono<ThreadDTO> addMessage(String threadId, Message message, boolean anonymous/*, Authentication authentication*/) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(RestApiPaths.THREADS_id_MESSAGES)
                        .queryParam("anonymous", anonymous)
                        .build(threadId))
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .bodyToMono(ThreadDTO.class);
    }

    @Override
    public Mono<ThreadDTO> readMessage(String threadId, String messageId, boolean read, String userId) {
        return this.webClient.patch()
                .uri(uriBuilder ->
                        uriBuilder
                                .path(RestApiPaths.THREADS_id_MESSAGES_id)
                                .queryParam("read", read)
                                .queryParam("userId", userId)
                                .build(threadId, messageId)
                )
                .retrieve()
                .bodyToMono(ThreadDTO.class);
    }

    @Override
    public Mono<Integer> anonymizeUser(String email) {
        return this.webClient.post()
                .uri(RestApiPaths.USER_ANONYMIZE)
                .body(BodyInserters.fromValue(new AnonymizeUserRequest(email)))
                .retrieve()
                .bodyToMono(Integer.class);
    }

    private Mono<UnreadThreads> getUnreadThreads(List<String> groups, String email) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(RestApiPaths.INBOX_TOTAL_UNREAD)
                        .queryParam("groups", groups)
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(UnreadThreads.class);
    }

}
