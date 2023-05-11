package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.*;

import java.util.Date;
import java.util.List;

public class MessageDTO {
    private String id;
    private Correspondent from;
    private List<? extends Correspondent> to;
    private String body;
    private Date date;
    private boolean read = false;
    private Date readDate;

    public MessageDTO() {
    }

    public MessageDTO(StoredMessage sm) {
        this.id = sm.getId();
        this.from = sm.getMessage().getFrom();
        this.to = sm.getMessage().getTo();
        this.body = sm.getMessage().getBody();
        this.date = sm.getMessage().getDate();
        this.read = sm.getMetadata().isRead();
        this.readDate = sm.getMetadata().getReadDate();
    }

    public static StoredMessage toStoredMessage(MessageDTO message) {
        StoredMessage storedMessage = new StoredMessage();
        storedMessage.setId(message.getId());
        storedMessage.setMessage(new Message(message.getFrom(), message.getTo(), message.getBody(), message.getDate()));
        storedMessage.setMetadata(new Metadata(message.getFrom(), false, false, null));
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

    public List<? extends Correspondent> getTo() {
        return to;
    }

    public void setTo(List<? extends Correspondent> to) {
        this.to = to;
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

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }
}
