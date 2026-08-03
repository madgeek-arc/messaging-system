package gr.athenarc.messaging.config;


import gr.athenarc.messaging.service.MessagingService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(MessagingClientProperties.class)
public class MessagingClientConfig {

    /**
     * Registered unconditionally: a missing endpoint is safe at construction time, and
     * {@link MessagingClientProperties#checkMissing()} already warns about it at startup.
     * <p>
     * Named {@code messagingClient} rather than {@code messagingService}: the latter is a
     * generic enough name that consumers plausibly have their own bean under it, and a clash
     * would fail their context at startup with a bean definition override error.
     */
    @Bean
    @ConditionalOnMissingBean
    public MessagingService messagingClient(MessagingClientProperties messagingClientProperties) {
        return new MessagingService(messagingClientProperties);
    }
}
