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
