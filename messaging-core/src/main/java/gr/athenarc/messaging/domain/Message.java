package gr.athenarc.messaging.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

    private Correspondent from;
    private List<Correspondent> to;
    private String body;
    private Date date;

    public Message() {
        // no-arg constructor
    }

    public Message(Correspondent from, List<Correspondent> to, String body, Date date) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
    }

    public Message(final Message that) {
        this(that.getFrom(), new ArrayList<>(that.getTo()), that.getBody(), that.date != null ? new Date(that.date.getTime()) : new Date());
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
}
