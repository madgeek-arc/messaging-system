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

package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadDTO {

    private String id;
    private String subject;
    private List<String> tags;
    private Correspondent from;
    private List<Correspondent> to;
    private List<MessageDTO> messages;
    private Date created;
    private Date updated;
    private boolean read;

    public ThreadDTO() {
    }

    public ThreadDTO(TopicThread thread, String userId) {
        this.id = thread.getId();
        this.subject = thread.getSubject();
        this.tags = thread.getTags();
        this.from = thread.getFrom();
        this.to = thread.getTo();
        this.messages = thread.getMessages().stream().map(message -> new MessageDTO(message, userId)).collect(Collectors.toList());
        this.created = thread.getCreated();
        this.updated = thread.getUpdated();
        this.read = !thread.getMessages().parallelStream().map(StoredMessage::getMetadata).anyMatch(m -> !m.getReadBy().contains(userId));
    }

    public static TopicThread toTopicThread(ThreadDTO thread) {
        TopicThread topicThread = new TopicThread();
        topicThread.setId(thread.getId());
        topicThread.setSubject(thread.getSubject());
        topicThread.setTags(thread.getTags());
        topicThread.setFrom(thread.getFrom());
        topicThread.setTo(thread.getTo());
        if (thread.getMessages() != null) {
            topicThread.setMessages(thread.getMessages().stream().map(MessageDTO::toStoredMessage).collect(Collectors.toList()));
        }
        topicThread.setCreated(thread.getCreated());
        topicThread.setUpdated(thread.getUpdated());
        return topicThread;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Correspondent getFrom() {
        return from;
    }

    public void setFrom(Correspondent from) {
        this.from = from;
    }

    public List<Correspondent> getTo() {
        return to;
    }

    public void setTo(List<Correspondent> to) {
        this.to = to;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean unread) {
        this.read = unread;
    }
}
