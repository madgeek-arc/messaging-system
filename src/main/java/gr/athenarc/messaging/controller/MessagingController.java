package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.domain.*;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.repository.TopicThreadRepository;
import gr.athenarc.messaging.service.TopicThreadService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@RestController
public class MessagingController {

    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    private final TopicThreadRepository topicThreadRepository;
    private final TopicThreadService topicThreadService;

    public MessagingController(TopicThreadRepository topicThreadRepository,
                               TopicThreadService topicThreadService) {
        this.topicThreadRepository = topicThreadRepository;
        this.topicThreadService = topicThreadService;
    }

    @GetMapping("threads/{threadId}")
    public Mono<ThreadDTO> get(@PathVariable String threadId) {
        return topicThreadService.get(threadId).map(ThreadDTO::new);
    }

    @GetMapping("threads/from")
    public Flux<ThreadDTO> getThreadsFrom(@RequestParam String email) {
        return getByEmail(email).map(ThreadDTO::new);
    }

    @GetMapping("threads/to")
    public Flux<ThreadDTO> getThreadsTo(@RequestParam String group) {
        return getByGroup(group).map(ThreadDTO::new);
    }

    public Flux<TopicThread> getByEmail(String email) {
        TopicThread topicThread = new TopicThread();
        Correspondent user = new Correspondent();
        user.setEmail(email);
        topicThread.setFrom(user);
        return topicThreadRepository.findAll(Example.of(topicThread), Sort.by(Sort.Order.desc("updated")));
    }

    public Flux<TopicThread> getByGroup(String group) {
        TopicThread topicThread = new TopicThread();
        Correspondent userGroup = new Correspondent();
        userGroup.setGroupId(group);
        topicThread.setTo(List.of(userGroup));
        return topicThreadRepository.findAll(Example.of(topicThread), Sort.by(Sort.Order.desc("updated")));
    }

    @GetMapping("threads")
    public Flux<ThreadDTO> getTopics(
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        return topicThreadService.browse(Sort.by(direction, sortBy)).map(ThreadDTO::new);
    }

    @PostMapping("threads/search")
    public Flux<ThreadDTO> getTopicsByExample(
            @RequestBody(required = false) ThreadDTO threadDTO,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        return topicThreadService.browse(
                    Example.of(ThreadDTO.toTopicThread(threadDTO)),
                    Sort.by(direction, sortBy)
                ).map(ThreadDTO::new);
    }

    @PostMapping("threads")
    public Mono<ThreadDTO> add(@RequestBody ThreadDTO thread) {
        return topicThreadService.add(ThreadDTO.toTopicThread(thread)).map(ThreadDTO::new);
    }

    @PostMapping("threads/dummy")
    public Mono<ThreadDTO> addDummy() {
        return topicThreadRepository.save(createThread()).map(ThreadDTO::new);
    }

    @PutMapping("threads/{threadId}")
    public Mono<ThreadDTO> update(@PathVariable String threadId, @RequestBody TopicThread topicThread) {
        return topicThreadService.update(threadId, topicThread).map(ThreadDTO::new);
    }

    @PostMapping("threads/{threadId}/messages")
    public Mono<ThreadDTO> addMessage(@PathVariable String threadId, @RequestBody Message message, @RequestParam(defaultValue = "false") boolean anonymous) {
        return topicThreadService.addMessage(threadId, message, anonymous).map(ThreadDTO::new);
    }

    @DeleteMapping("threads/{threadId}")
    public Mono<Void> delete(@PathVariable String threadId) {
        return topicThreadService.delete(threadId);
    }

    @DeleteMapping("threads/")
    public Mono<Void> deleteAll() {
        return topicThreadRepository.deleteAll();
    }

    @PatchMapping("threads/{threadId}/messages/{messageId}")
    public Mono<ThreadDTO> readMessage(@PathVariable String threadId, @PathVariable String messageId, @RequestParam("read") boolean read) {
        return topicThreadService.readMessage(threadId, messageId, read).map(ThreadDTO::new);
    }


    private TopicThread createThread() {
        TopicThread topicThread = new TopicThread();
        topicThread.setMessages(null);
        topicThread.setSubject("Test");
        topicThread.setCreated(new Date());
        topicThread.setUpdated(new Date());
        topicThread.setTags(List.of("test", "develop"));

        Correspondent from = new Correspondent();
        from.setEmail("test@email.com");
        from.setName("Unknown User");
        from.setGroupId("group");
        topicThread.setFrom(from);

        Correspondent to = new Correspondent();
        to.setGroupId("developers");
        to.setName("");
        topicThread.setTo(List.of(to));

        Message message = new Message();
        message.setBody("This is a test message to check if it is saved successfully");
        message.setDate(new Date());
        message.setFrom(from);
        message.setTo(List.of(to));

        Metadata metadata = new Metadata();
        metadata.setSentBy(from);
        metadata.setAnonymousSender(false);

        StoredMessage storedMessage = new StoredMessage();
//        storedMessage.setId("0");
        storedMessage.setMessage(message);
        storedMessage.setMetadata(metadata);

        Message reply = new Message();
        reply.setBody("Hello, your message was saved successfully");
        reply.setDate(new Date());
        reply.setFrom(to);
        reply.setTo(List.of(from));

        Metadata replyMetadata = new Metadata();
        replyMetadata.setAnonymousSender(true);
        Correspondent respondent = new Correspondent();
        respondent.setEmail("developer@example.email");
        respondent.setName("Random Developer X");
        respondent.setGroupId("developers");
        replyMetadata.setSentBy(respondent);

        StoredMessage storedReply = new StoredMessage();
//        storedReply.setId("1");
        storedReply.setMessage(reply);
        storedReply.setMetadata(replyMetadata);

        topicThread.setMessages(List.of(storedMessage, storedReply, storedMessage, storedReply, storedMessage, storedReply, storedMessage, storedReply));

        return topicThread;
    }

}
