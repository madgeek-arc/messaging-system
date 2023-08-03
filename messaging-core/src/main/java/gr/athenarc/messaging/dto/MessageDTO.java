package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.Metadata;
import gr.athenarc.messaging.domain.StoredMessage;

import java.util.Date;
import java.util.List;

public class MessageDTO {
    private String id;
    private Correspondent from;
    private List<Correspondent> to;
    private boolean anonymousSender;
    private String body;
    private Date date;
    private boolean read = false;
    private String replyToMessageId;

    public MessageDTO() {
    }

    public MessageDTO(StoredMessage sm, String userId) {
        this.id = sm.getId();
        this.from = sm.getMessage().getFrom();
        this.to = sm.getMessage().getTo();
        this.anonymousSender = sm.getMetadata().isAnonymousSender();
        this.body = sm.getMessage().getBody();
        this.date = sm.getMessage().getDate();
        this.read = sm.getMetadata().getReadBy().contains(userId);
        this.replyToMessageId = sm.getMessage().getReplyToMessageId();
    }

    public static StoredMessage toStoredMessage(MessageDTO message) {
        StoredMessage storedMessage = new StoredMessage();
        storedMessage.setId(message.getId());
        storedMessage.setMessage(new Message(message.getFrom(), message.getTo(), message.getBody(), message.getDate(), message.getReplyToMessageId()));
        storedMessage.setMetadata(new Metadata(message.getFrom(), message.isAnonymousSender()));
        return storedMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAnonymousSender() {
        return anonymousSender;
    }

    public void setAnonymousSender(boolean anonymousSender) {
        this.anonymousSender = anonymousSender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
}
