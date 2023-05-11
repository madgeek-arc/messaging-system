package gr.athenarc.messaging;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.TopicThread;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class MessagingSystemApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void createThread() {
		TopicThread topicThread = new TopicThread();
		topicThread.setMessages(null);
		topicThread.setSubject("Test");
		topicThread.setCreated(new Date());
		topicThread.setUpdated(new Date());
		topicThread.setTags(List.of("test", "develop"));

		Correspondent from = new Correspondent();
		from.setEmail("test@email.com");
		from.setName("Unknown User");
		topicThread.setFrom(from);

		Correspondent to = new Correspondent();
		to.setGroupId("developers");
		to.setName("Developers");
		topicThread.setTo(List.of(to));
	}
}
