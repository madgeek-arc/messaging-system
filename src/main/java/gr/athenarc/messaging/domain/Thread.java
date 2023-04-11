package gr.athenarc.messaging.domain;

import java.util.Date;
import java.util.List;

public class Thread {

    private String id;
    private String subject;
    private String[] tags;
    private Correspondent from;
    private Correspondent to;
    private StoredMessage[] messages;
    private Date created;
    private Date updated;

    public Thread() {
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

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
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

    public StoredMessage[] getMessages() {
        return messages;
    }

    public void setMessages(StoredMessage[] messages) {
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
