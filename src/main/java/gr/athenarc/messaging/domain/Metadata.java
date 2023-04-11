package gr.athenarc.messaging.domain;

public class Metadata {

    private User sentBy;
    private boolean anonymousSender;

    public Metadata() {
        // no-arg constructor
    }

    public User getSentBy() {
        return sentBy;
    }

    public void setSentBy(User sentBy) {
        this.sentBy = sentBy;
    }

    public boolean isAnonymousSender() {
        return anonymousSender;
    }

    public void setAnonymousSender(boolean anonymousSender) {
        this.anonymousSender = anonymousSender;
    }
}
