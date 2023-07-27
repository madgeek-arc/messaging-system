package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.domain.*;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadMessages;
import gr.athenarc.messaging.repository.TopicThreadRepository;
import gr.athenarc.messaging.service.TopicThreadService;
import gr.athenarc.recaptcha.annotation.ReCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadService.browse(Sort.by(direction, sortBy)).map(ThreadDTO::new);
    }

    @GetMapping("threads/search")
    public Flux<?> search(
            @RequestParam String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadRepository.findAllUsingQuery(regex, PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }

    @GetMapping("inbox/unread")
    @PreAuthorize("isAuthenticated()")
    public Mono<UnreadMessages> searchTotalUnread(@RequestParam List<String> groups) {
        return Mono.just(UnreadMessages.of(groups, Objects.requireNonNull(topicThreadRepository.searchUnread(groups, PageRequest.of(0, 1_000_000)).toStream().collect(Collectors.toList()))));
    }

    @GetMapping("inbox/threads/search")
    @PreAuthorize("isAuthenticated()")
    public Flux<?> searchInbox(
            @RequestParam String groupId,
            @RequestParam(defaultValue = "") String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            /*@Parameter(hidden = true) */ Authentication authentication) {
        logger.info(authentication.toString());
        return topicThreadRepository.searchInbox(groupId, regex, PageRequest.of(page, size, Sort.by(direction, sortBy))).map(ThreadDTO::new);
    }

    @GetMapping("inbox/threads/unread")
    @PreAuthorize("isAuthenticated()")
    public Flux<?> searchUnread(
            @RequestParam List<String> groups,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            /*@Parameter(hidden = true) */ Authentication authentication) {
        logger.info(authentication.toString());
        return topicThreadRepository.searchUnread(groups, PageRequest.of(page, size, Sort.by(direction, sortBy))).map(ThreadDTO::new);
    }

    @GetMapping("outbox/threads/search")
    @PreAuthorize("isAuthenticated()")
    public Flux<?> searchOutbox(
            @RequestParam String groupId,
            @RequestParam String email,
            @RequestParam(defaultValue = "") String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            /*@Parameter(hidden = true)*/ @AuthenticationPrincipal Authentication authentication) {
        logger.info(authentication.toString());
        return topicThreadRepository.searchOutbox(groupId, regex, email, PageRequest.of(page, size, Sort.by(direction, sortBy))).map(ThreadDTO::new);
    }

    @GetMapping("threads/tags")
    public Flux<?> searchTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadRepository.findAllByTagsContainingIgnoreCase(tags, PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }

    @GetMapping("threads/subject")
    public Flux<?> searchSubject(
            @RequestParam String subject,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadRepository.findAllBySubjectContainingIgnoreCase(subject, PageRequest.of(page, size, Sort.by(direction, sortBy)));
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

    @ReCaptcha("#recaptcha")
    @PostMapping("threads")
    public Mono<ThreadDTO> addExternal(@RequestHeader("g-recaptcha-response") String recaptcha, @RequestBody ThreadDTO thread) {
        return topicThreadService.add(ThreadDTO.toTopicThread(thread)).map(ThreadDTO::new);
    }

    // TODO: uncomment method for authenticated users ?
    @PostMapping("threads/internal")
    @PreAuthorize("isAuthenticated()")
    public Mono<ThreadDTO> add(@RequestBody ThreadDTO thread) {
        return topicThreadService.add(ThreadDTO.toTopicThread(thread)).map(ThreadDTO::new);
    }

    @PostMapping("threads/dummy")
    public Mono<ThreadDTO> addDummy() {
        return topicThreadRepository.save(createThread()).map(ThreadDTO::new);
    }

    @PutMapping("threads/{threadId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ThreadDTO> update(@PathVariable String threadId, @RequestBody TopicThread topicThread) {
        return topicThreadService.update(threadId, topicThread).map(ThreadDTO::new);
    }

    @PostMapping("threads/{threadId}/messages")
    @PreAuthorize("isAuthenticated() and #message.from.email == #authentication.principal.getAttribute('email')")
    public Mono<ThreadDTO> addMessage(
            @PathVariable String threadId,
            @RequestBody Message message,
            @RequestParam(defaultValue = "false") boolean anonymous,
            /*@Parameter(hidden = true)*/ @AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
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
