/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.messaging.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

import java.time.Duration;

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

        /**
         * Key sent in the X-API-Key header, required when the service sets messaging-system.api-key.
         */
        private String apiKey;

        /**
         * Time allowed to establish the connection to the messaging service.
         */
        private Duration connectTimeout = Duration.ofSeconds(10);

        /**
         * Time allowed for the messaging service to respond.
         */
        private Duration responseTimeout = Duration.ofSeconds(30);

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Duration getResponseTimeout() {
            return responseTimeout;
        }

        public void setResponseTimeout(Duration responseTimeout) {
            this.responseTimeout = responseTimeout;
        }
    }

    @PostConstruct
    void checkMissing() {
        if (this.getClient() == null || !StringUtils.hasText(this.getClient().getEndpoint())) {
            logger.warn("Missing property: 'messaging-system.client.endpoint'");
        }
    }
}
