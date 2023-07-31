package gr.athenarc.messaging.controller;

import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.dto.UnreadMessages;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static gr.athenarc.messaging.controller.RestApiPaths.*;

@RestController
public interface TopicThreadsController {


    @GetMapping(THREADS_id)
    Mono<ThreadDTO> get(@PathVariable String threadId);

    @PostMapping(THREADS)
    Mono<ThreadDTO> addExternal(@RequestHeader("g-recaptcha-response") String recaptcha, @RequestBody ThreadDTO thread);

    @PostMapping(THREADS_INTERNAL)
    Mono<ThreadDTO> add(@RequestBody ThreadDTO thread);

    @PutMapping(THREADS_PUT_ID)
    Mono<ThreadDTO> update(@PathVariable String threadId, @RequestBody TopicThread topicThread);

    @DeleteMapping(THREADS_id)
    Mono<Void> delete(@PathVariable String threadId);

    @GetMapping(INBOX_TOTAL_UNREAD)
    Mono<UnreadMessages> searchTotalUnread(@RequestParam List<String> groups);

    @GetMapping(INBOX_THREADS_SEARCH)
    Flux<ThreadDTO> searchInbox(
            @RequestParam String groupId,
            @RequestParam(defaultValue = "") String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping(INBOX_THREADS_UNREAD)
    Flux<ThreadDTO> searchInboxUnread(
            @RequestParam List<String> groups,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            Authentication authentication);

    @GetMapping(OUTBOX_THREADS_SEARCH)
    Flux<ThreadDTO> searchOutbox(
            @RequestParam String groupId,
            @RequestParam String email,
            @RequestParam(defaultValue = "") String regex,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size/*,
            Authentication authentication*/);

    @PostMapping(THREADS_id_MESSAGES)
    Mono<ThreadDTO> addMessage(
            @PathVariable String threadId,
            @RequestBody Message message,
            @RequestParam(defaultValue = "false") boolean anonymous,
            Authentication authentication);

    @PatchMapping(THREADS_id_MESSAGES_id)
    Mono<ThreadDTO> readMessage(@PathVariable String threadId, @PathVariable String messageId, @RequestParam("read") boolean read);

//    @GetMapping(THREADS_FROM)
//    Flux<ThreadDTO> getThreadsFrom(@RequestParam String email);
//
//    @GetMapping(THREADS_TO)
//    Flux<ThreadDTO> getThreadsTo(@RequestParam String group);
//
//    @GetMapping(THREADS)
//    public Flux<ThreadDTO> getTopics(
//            @RequestParam(defaultValue = "created") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size);
//
//    @GetMapping(THREADS_SEARCH)
//    Flux<?> search(
//            @RequestParam String regex,
//            @RequestParam(defaultValue = "created") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size);
//
//    @GetMapping(THREADS_SEARCH_TAGS_SUBJECT)
//    Flux<?> searchSubjectAndTags(
//            @RequestParam List<String> tags,
//            @RequestParam(defaultValue = "") String subject,
//            @RequestParam(defaultValue = "created") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size);
//
//    @PostMapping(THREADS_BY_EXAMPLE)
//    Flux<ThreadDTO> getTopicsByExample(
//            @RequestBody(required = false) ThreadDTO threadDTO,
//            @RequestParam(defaultValue = "created") String sortBy,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction);


}

