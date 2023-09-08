package gr.athenarc.messaging.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingClientProperties.class)
public class MessagingClientConfig {

}
