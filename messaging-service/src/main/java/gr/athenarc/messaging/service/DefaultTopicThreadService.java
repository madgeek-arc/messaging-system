/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.messaging.service;

import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;
import gr.athenarc.messaging.dto.ThreadDTO;
import gr.athenarc.messaging.privacy.PersonalDataEraser;
import gr.athenarc.messaging.repository.ReactiveMongoTopicThreadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bson.Document;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultTopicThreadService implements TopicThreadService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTopicThreadService.class);

    private final ReactiveMongoTopicThreadRepository topicThreadRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    public DefaultTopicThreadService(ReactiveMongoTopicThreadRepository topicThreadRepository,
                                     ReactiveMongoTemplate mongoTemplate) {
        this.topicThreadRepository = topicThreadRepository;
        this.mongoTemplate = mongoTemplate;
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
                topicThread.getMessages().get(i).getMetadata().getReadBy().add(topicThread.getFrom().getEmail());
            }
            logger.warn("Adding new Thread with {} messages", topicThread.getMessages().size());
        }
        return topicThreadRepository.save(topicThread);
    }

    @Override
    public Mono<TopicThread> update(String id, TopicThread topicThread) {
        return topicThreadRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Not Found")))
                .flatMap(s -> topicThreadRepository.save(topicThread));
    }

    @Override
    public Mono<Void> delete(String id) {
        return topicThreadRepository.deleteById(id);
    }

    @Override
    public Mono<TopicThread> getByIdAndUserEmailOrGroup(String threadId, String email, String groupId) {
        return topicThreadRepository.findByIdAndUserOrGroup(threadId, email, groupId);
    }

    @Override
    public Flux<TopicThread> browse(Sort sort) {
        return topicThreadRepository.findAll(sort);
    }

    @Override
    public Flux<TopicThread> browse(Example<TopicThread> example, Sort sort) {
        return topicThreadRepository.findAll(example, sort);
    }

    /**
     * Appends the message in a single pipeline update that sets its id to the thread's current
     * message count, so concurrent appends neither overwrite each other nor share an id.
     */
    @Override
    public Mono<TopicThread> addMessage(String threadId, Message message, boolean anonymousSender) {
        if (message == null || message.getFrom() == null || !StringUtils.hasText(message.getFrom().getEmail())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "message.from.email is required"));
        }
        StoredMessage storedMessage = StoredMessage.of(message, anonymousSender);
        storedMessage.getMetadata().getReadBy().add(message.getFrom().getEmail());

        MongoConverter converter = mongoTemplate.getConverter();
        Document stored = (Document) converter.convertToMongoType(storedMessage, TypeInformation.of(StoredMessage.class));
        String idField = converter.getMappingContext().getRequiredPersistentEntity(StoredMessage.class)
                .getRequiredIdProperty().getFieldName();
        Document messages = new Document("$ifNull", Arrays.asList("$messages", List.of()));
        Document appended = new Document("$mergeObjects", List.of(
                new Document("$literal", stored),
                new Document(idField, new Document("$toString", new Document("$size", messages)))));
        AggregationUpdate update = AggregationUpdate.from(List.of(context -> new Document("$set",
                new Document("messages", new Document("$concatArrays", List.of(messages, List.of(appended)))))));

        return mongoTemplate.findAndModify(Query.query(Criteria.where("_id").is(threadId)), update,
                        FindAndModifyOptions.options().returnNew(true), TopicThread.class)
                .switchIfEmpty(Mono.error(() -> threadNotFound(threadId)));
    }

    /**
     * Adds or removes {@code userId} in the read receipts of the message at index {@code messageId}
     * with a single {@code $addToSet} or {@code $pull}.
     */
    @Override
    public Mono<TopicThread> readMessage(String threadId, String messageId, boolean read, String userId) {
        if (!StringUtils.hasText(userId)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required"));
        }
        int index;
        try {
            index = Integer.parseInt(messageId);
        } catch (NumberFormatException e) {
            index = -1;
        }
        if (index < 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message id: " + messageId));
        }
        String messagePath = "messages." + index;
        String readByPath = messagePath + ".metadata.readBy";
        Query query = Query.query(Criteria.where("_id").is(threadId).and(messagePath).exists(true));
        Update update = read ? new Update().addToSet(readByPath, userId) : new Update().pull(readByPath, userId);
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), TopicThread.class)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Message " + messageId + " not found in thread " + threadId)));
    }

    private static ResponseStatusException threadNotFound(String threadId) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Thread not found: " + threadId);
    }


    @Override
    public Mono<Page<ThreadDTO>> getInbox(String groupId, String regex, String email, Pageable pageable) {
        String pattern = SearchTerms.literal(regex);
        return topicThreadRepository.searchInbox(groupId, pattern, email, pageable)
                .filter(Objects::nonNull)
                .map(topic -> new ThreadDTO(topic, email))
                .collectList()
                .zipWith(topicThreadRepository.countInbox(groupId, pattern, email))
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    /**
     * Threads whose personal data is already erased are filtered out before the save, so a repeat
     * call is a no-op that reports 0. The 'updated' timestamp is deliberately not touched: every
     * inbox query sorts by it, and bumping it would float a purged user's oldest threads back to
     * the top of everyone else's inbox.
     */
    @Override
    public Mono<Integer> anonymizeUser(String email) {
        // Anchored and quoted so that the '.' and '+' that are commonplace in email addresses are
        // matched literally rather than as regex metacharacters.
        String emailRegex = "^" + Pattern.quote(email) + "$";
        return topicThreadRepository.findAllByCorrespondentEmail(emailRegex)
                .filter(thread -> PersonalDataEraser.anonymize(thread, email))
                .flatMap(topicThreadRepository::save)
                .count()
                .map(Long::intValue)
                // Deliberately omits the email: logging the identifier being erased would itself
                // retain the personal data this method exists to remove.
                .doOnSuccess(count -> logger.info("Erased personal data from {} thread(s).", count));
    }
}
