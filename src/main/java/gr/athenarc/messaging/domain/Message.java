package gr.athenarc.messaging.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

    private Correspondent from;
    private List<? extends Correspondent> to;
    private String body;
    private Date date;

    public Message() {
        // no-arg constructor
    }

    public Message(Correspondent from, List<? extends Correspondent> to, String body, Date date) {
        this.from = from;
        this.to = to;
        this.body = body;
        this.date = date;
    }

    public Message(final Message that) {
        this(that.getFrom(), new ArrayList<>(that.getTo()), that.getBody(), new Date(that.date.getTime()));
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
}
