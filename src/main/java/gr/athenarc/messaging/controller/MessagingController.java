package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.domain.*;
import gr.athenarc.messaging.repository.TopicThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@RestController
public class MessagingController {

    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    private final TopicThreadRepository topicThreadRepository;

    public MessagingController(TopicThreadRepository topicThreadRepository) {
        this.topicThreadRepository = topicThreadRepository;
    }

    @GetMapping("threads/{id}")
    public Mono<TopicThread> get(@PathVariable String id) {
        return topicThreadRepository.findById(id);
    }

    @GetMapping("threads")
    public Flux<TopicThread> getTopics() {
        return topicThreadRepository.findAll();
    }

    @PostMapping("threads")
    public Mono<TopicThread> add(@RequestBody TopicThread topicThread) {
        return topicThreadRepository.save(topicThread);
    }

    @PostMapping("threads/dummy")
    public Mono<TopicThread> addDummy() {
        return topicThreadRepository.save(createThread());
    }

    @PutMapping("threads/{id}")
    public Mono<TopicThread> add(@PathVariable String id, @RequestBody TopicThread topicThread) {
        return topicThreadRepository.save(topicThread);
    }

    @PostMapping("threads/{id}/messages")
    public Mono<TopicThread> addMessage(@PathVariable String id, @RequestBody Message message, @RequestParam(defaultValue = "false") boolean anonymous) {
        return topicThreadRepository.findById(id).flatMap(
                topic -> {
                    StoredMessage storedMessage = new StoredMessage();
                    storedMessage.setId(String.valueOf(topic.getMessages().size()));
                    storedMessage.setMessage(message);

                    Metadata metadata = new Metadata();
                    User user = new User();
                    user.setName(message.getFrom().getName());
                    user.setEmail("test@test.test");
                    metadata.setSentBy(user);
                    metadata.setAnonymousSender(anonymous);

                    storedMessage.setMetadata(metadata);
                    topic.getMessages().add(storedMessage);
                    return topicThreadRepository.save(topic);
                }
        );

    }

    @DeleteMapping("threads/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return topicThreadRepository.deleteById(id);
    }

    @DeleteMapping("threads/")
    public Mono<Void> deleteAll() {
        return topicThreadRepository.deleteAll();
    }


    private TopicThread createThread() {
        TopicThread topicThread = new TopicThread();
        topicThread.setMessages(null);
        topicThread.setSubject("Test");
        topicThread.setCreated(new Date());
        topicThread.setUpdated(new Date());
        topicThread.setTags(List.of("test", "develop"));

        Correspondent from = new User();
        ((User) from).setEmail("test@email.com");
        from.setName("Unknown User");
        topicThread.setFrom(from);

        Correspondent to = new UserGroup();
        ((UserGroup) to).setGroupId("developers");
        to.setName("Developers");
        topicThread.setTo(to);

        Message message = new Message();
        message.setBody("This is a test message to check if it is saved successfully");
        message.setDate(new Date());
        message.setFrom(from);
        message.setTo(List.of(to));

        Metadata metadata = new Metadata();
        metadata.setSentBy((User) from);
        metadata.setAnonymousSender(false);

        StoredMessage storedMessage = new StoredMessage();
        storedMessage.setId("0");
        storedMessage.setMessage(message);
        storedMessage.setMetadata(metadata);

        Message reply = new Message();
        reply.setBody("Hello, your message was saved successfully");
        reply.setDate(new Date());
        reply.setFrom(to);
        reply.setTo(List.of(from));

        Metadata replyMetadata = new Metadata();
        replyMetadata.setAnonymousSender(true);
        User respondent = new User();
        respondent.setEmail("developer@example.email");
        respondent.setName("Random Developer X");
        replyMetadata.setSentBy(respondent);

        StoredMessage storedReply = new StoredMessage();
        storedReply.setId("1");
        storedReply.setMessage(reply);
        storedReply.setMetadata(replyMetadata);

        topicThread.setMessages(List.of(storedMessage, storedReply));

        return topicThread;
    }

}
