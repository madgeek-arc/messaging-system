package gr.athenarc.messaging.service;

import gr.athenarc.messaging.config.MessagingProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Deprecated
@Service
public class GroupService {

    private final MessagingProperties properties;

    public GroupService(MessagingProperties properties) {
        this.properties = properties;
    }

    public Mono<?> getGroups(String type) {
        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getUserGroups().getGroups().getEndpoint())
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1000 * 1024))
                .build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("type", type).build())
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Map.class));
    }

    public Mono<List> getUserEmails(String groupId) {
        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getUserGroups().getResolveEmails().getEndpoint())
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1000 * 1024))
                .build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("groupId", groupId).build())
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(List.class));
    }
}
