package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadMessages;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import gr.athenarc.messaging.service.TopicThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static gr.athenarc.messaging.controller.RestApiPaths.THREADS;

@RestController
public class MessagingController implements TopicThreadsController {

    private static final Logger logger = LoggerFactory.getLogger(MessagingController.class);

    private final ReactiveMongoTopicThreadRepository topicThreadRepository;
    private final TopicThreadService topicThreadService;

    public MessagingController(ReactiveMongoTopicThreadRepository topicThreadRepository,
                               TopicThreadService topicThreadService) {
        this.topicThreadRepository = topicThreadRepository;
        this.topicThreadService = topicThreadService;
    }

    @Override
    public Mono<ThreadDTO> get(@PathVariable String threadId) {
        return topicThreadService.get(threadId).map(ThreadDTO::new);
    }

    @Override
    @PostMapping(THREADS)
    public Mono<ThreadDTO> addExternal(@RequestHeader("g-recaptcha-response") String recaptcha, @RequestBody ThreadDTO thread) {
        return topicThreadService.add(ThreadDTO.toTopicThread(thread)).map(ThreadDTO::new);
    }

    // TODO: uncomment method for authenticated users ?
    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<ThreadDTO> add(@RequestBody ThreadDTO thread) {
        return topicThreadService.add(ThreadDTO.toTopicThread(thread)).map(ThreadDTO::new);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ThreadDTO> update(@PathVariable String threadId, @RequestBody TopicThread topicThread) {
        return topicThreadService.update(threadId, topicThread).map(ThreadDTO::new);
    }

    @Override
    @PreAuthorize("isAuthenticated() and #message.from.email == #authentication.principal.getAttribute('email')")
    public Mono<ThreadDTO> addMessage(
            @PathVariable String threadId,
            @RequestBody Message message,
            @RequestParam(defaultValue = "false") boolean anonymous,
            /*@Parameter(hidden = true)*/ @AuthenticationPrincipal Authentication authentication) {
        return topicThreadService.addMessage(threadId, message, anonymous).map(ThreadDTO::new);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<Void> delete(@PathVariable String threadId) {
        return topicThreadService.delete(threadId);
    }

    @Override
    public Mono<ThreadDTO> readMessage(@PathVariable String threadId, @PathVariable String messageId, @RequestParam("read") boolean read) {
        return topicThreadService.readMessage(threadId, messageId, read).map(ThreadDTO::new);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Mono<UnreadMessages> searchTotalUnread(@RequestParam List<String> groups) {
        return Mono.just(UnreadMessages.of(groups, Objects.requireNonNull(topicThreadRepository.searchUnread(groups, PageRequest.of(0, 1_000_000)).toStream().collect(Collectors.toList()))));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Flux<ThreadDTO> searchInbox(
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

    @Override
    @PreAuthorize("isAuthenticated()")
    public Flux<ThreadDTO> searchInboxUnread(
            @RequestParam List<String> groups,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            /*@Parameter(hidden = true) */ Authentication authentication) {
        logger.info(authentication.toString());
        return topicThreadRepository.searchUnread(groups, PageRequest.of(page, size, Sort.by(direction, sortBy))).map(ThreadDTO::new);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Flux<ThreadDTO> searchOutbox(
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

    //    @Override
    public Flux<ThreadDTO> getThreadsFrom(@RequestParam String email) {
        return getByEmail(email).map(ThreadDTO::new);
    }

    //    @Override
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

    //    @Override
    public Flux<?> searchSubjectAndTags(
            @RequestParam List<String> tags,
            @RequestParam(defaultValue = "") String subject,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        if (tags != null && !tags.isEmpty()) {
            return topicThreadRepository.findAllByTagsContainingIgnoreCaseAndSubjectContainingIgnoreCase(tags, subject, pageable);
        } else {
            return topicThreadRepository.findAllBySubjectContainingIgnoreCase(subject, pageable);
        }
    }

    //    @Override
    public Flux<ThreadDTO> getTopicsByExample(
            @RequestBody(required = false) ThreadDTO threadDTO,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        return topicThreadService.browse(
                Example.of(ThreadDTO.toTopicThread(threadDTO)),
                Sort.by(direction, sortBy)
        ).map(ThreadDTO::new);
    }

    //    @Override
    public Flux<ThreadDTO> getTopics(
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadService.browse(Sort.by(direction, sortBy)).map(ThreadDTO::new);
    }

    //    @Override
    public Flux<?> search(
            @RequestParam String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return topicThreadRepository.findAllUsingQuery(regex, PageRequest.of(page, size, Sort.by(direction, sortBy)));
    }


}
