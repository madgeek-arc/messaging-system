package gr.athenarc.messaging.domain;

public class Message {

    private Correspondent from;
    private Correspondent to;
    private String body;
    private String date;

    public Message() {
        // no-arg constructor
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
