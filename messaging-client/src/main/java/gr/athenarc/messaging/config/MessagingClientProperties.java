package gr.athenarc.messaging.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@ConfigurationProperties(prefix = "messaging-system")
public class MessagingClientProperties {

    private static final Logger logger = LoggerFactory.getLogger(MessagingClientProperties.class);

    private Client client = new Client();

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public static class Client {

        /**
         * Provide the endpoint of the messaging service (e.g. <a href="">https://messaging-service-host:port/api/</a>).
         */
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

    @PostConstruct
    void checkMissing() {
        if (this.getClient() == null || !StringUtils.hasText(this.getClient().getEndpoint())) {
            logger.warn("Missing property: 'messaging-system.client.endpoint'");
        }
    }
}
