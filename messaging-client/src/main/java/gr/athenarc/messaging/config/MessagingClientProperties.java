package gr.athenarc.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "messaging-system")
public class MessagingClientProperties {

    private Client client;

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
}
