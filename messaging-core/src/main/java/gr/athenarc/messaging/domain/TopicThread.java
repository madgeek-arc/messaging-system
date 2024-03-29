package gr.athenarc.messaging.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
public class TopicThread {

    @Id
    private String id;
    @Indexed
    private String subject;
    @Indexed
    private List<String> tags;
    private Correspondent from;
    private List<Correspondent> to;
    private List<StoredMessage> messages;
    private Date created;
    private Date updated;

    public TopicThread() {
        // no-arg constructor
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

    public void addMessage(StoredMessage storedMessage) {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        storedMessage.setId(String.valueOf(this.messages.size()));
        this.messages.add(storedMessage);
    }

    public List<StoredMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<StoredMessage> messages) {
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
}
