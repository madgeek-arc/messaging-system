package gr.athenarc.messaging;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.domain.User;
import gr.athenarc.messaging.domain.UserGroup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

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
		topicThread.setTags(new String[]{"test", "develop"});

		Correspondent from = new User();
		((User) from).setEmail("test@email.com");
		from.setName("Unknown User");
		topicThread.setFrom(from);

		Correspondent to = new UserGroup();
		((UserGroup) to).setGroupId("developers");
		to.setName("Developers");
		topicThread.setTo(to);
	}
}
