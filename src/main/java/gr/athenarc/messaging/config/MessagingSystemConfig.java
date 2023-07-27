package gr.athenarc.messaging.config;


import gr.athenarc.recaptcha.annotation.EnableReCaptcha;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableReCaptcha
@EnableRedisWebSession
public class MessagingSystemConfig {

}
