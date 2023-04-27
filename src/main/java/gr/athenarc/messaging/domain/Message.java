package gr.athenarc.messaging.domain;

import java.util.Date;
import java.util.List;

public class Message <K extends Correspondent> {

    private K from;
    private List<K> to;
    private String body;
    private Date date;

    public Message() {
        // no-arg constructor
    }

    public K getFrom() {
        return from;
    }

    public void setFrom(K from) {
        this.from = from;
    }

    public List<K> getTo() {
        return to;
    }

    public void setTo(List<K> to) {
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
