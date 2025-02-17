package gr.athenarc.messaging.config;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(MessagingClientProperties.class)
public class MessagingClientConfig {
}
