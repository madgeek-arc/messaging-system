package gr.athenarc.messaging.domain;

import java.util.Date;

public class Metadata {

    private User sentBy;
    private boolean anonymousSender;
    private boolean read = false;
    private Date readDate;

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
