package gr.athenarc.messaging.service;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.mailer.client.service.MailClient;
import gr.athenarc.messaging.mailer.domain.EmailMessage;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultTopicThreadService implements TopicThreadService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTopicThreadService.class);
    private final ReactiveMongoTopicThreadRepository topicThreadRepository;
    private final MailClient mailClient;
    private final GroupService groupService;

    public DefaultTopicThreadService(ReactiveMongoTopicThreadRepository topicThreadRepository,
                                     MailClient mailClient,
                                     GroupService groupService) {
        this.topicThreadRepository = topicThreadRepository;
        this.mailClient = mailClient;
        this.groupService = groupService;
    }

    @Override
    public Mono<TopicThread> get(String id) {
        return topicThreadRepository.findById(id);
    }

    @Override
    public Mono<TopicThread> add(TopicThread topicThread) {
        Date now = new Date();
        topicThread.setCreated(now);
        topicThread.setUpdated(now);
        if (topicThread.getMessages() != null) {
            for (int i = 0; i < topicThread.getMessages().size(); i++) {
                topicThread.getMessages().get(i).setId(String.valueOf(i));
                topicThread.getMessages().get(i).getMessage().setDate(now);
            }
            logger.warn("Adding new Thread with {} messages", topicThread.getMessages().size());
        }
        return topicThreadRepository.save(topicThread);
    }

    @Override
    public Mono<TopicThread> update(String id, TopicThread topicThread) {
        return topicThreadRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Not Found")))
                .doOnSuccess(s -> topicThreadRepository.save(topicThread));
    }

    @Override
    public Mono<Void> delete(String id) {
        return topicThreadRepository.deleteById(id);
    }

    @Override
    public Flux<TopicThread> browse(Sort sort) {
        return topicThreadRepository.findAll(sort);
    }

    @Override
    public Flux<TopicThread> browse(Example<TopicThread> example, Sort sort) {
        return topicThreadRepository.findAll(example, sort);
    }

    @Override
    public Mono<TopicThread> addMessage(String threadId, Message message, boolean anonymousSender) {
//        Set<String> groups = message.getTo().stream().map(Correspondent::getGroupId).filter(Objects::nonNull).collect(Collectors.toSet());
//        groups.add(message.getFrom().getGroupId());
//        Mono<List> mono = Mono.from(Flux.fromIterable(groups).flatMap(groupService::getUserEmails));
        return topicThreadRepository.findById(threadId).flatMap(
                        topic -> {
                            StoredMessage storedMessage = StoredMessage.of(message, anonymousSender);
                            topic.getMessages().add(storedMessage);
//                            sendEmails(topic, message, mono);
                            return topicThreadRepository.save(topic);
                        })
                .log();
    }

    private void sendEmails(TopicThread topic, Message message, Mono<List> bcc) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setFrom("test.openaire@gmail.com");
        emailMessage.setSubject(topic.getSubject());
        emailMessage.setText(message.getBody());

        emailMessage.setTo(message.getTo().stream().map(Correspondent::getEmail).collect(Collectors.toList()));
        emailMessage.setBcc(bcc.block());
        mailClient.sendMail(emailMessage);
    }

    @Override
    public Mono<TopicThread> readMessage(String threadId, String messageId, boolean read) {
        return get(threadId).flatMap(thread -> {
            StoredMessage message = thread.getMessages().get(Integer.parseInt(messageId));
            message.getMetadata().setRead(read);
            if (read) {
                message.getMetadata().setReadDate(new Date());
            }
            return this.topicThreadRepository.save(thread);
        });
    }


}
