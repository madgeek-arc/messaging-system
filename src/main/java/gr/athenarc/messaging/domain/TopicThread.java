package gr.athenarc.messaging.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class TopicThread {

    @Id
    private String id;
    private String subject;
    private List<String> tags;
    private Correspondent from;
    private Correspondent to;
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

    public Correspondent getTo() {
        return to;
    }

    public void setTo(Correspondent to) {
        this.to = to;
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
