package gr.athenarc.messaging.domain;

import java.util.Date;

public class Metadata {

    private Correspondent sentBy;
    private boolean anonymousSender;
    private boolean read = false;
    private Date readDate;

    public Metadata() {
        // no-arg constructor
    }

    public Metadata(Correspondent sentBy, boolean anonymousSender, boolean read, Date readDate) {
        this.sentBy = sentBy;
        this.anonymousSender = anonymousSender;
        this.read = read;
        this.readDate = readDate;
    }

    public Correspondent getSentBy() {
        return sentBy;
    }

    public void setSentBy(Correspondent sentBy) {
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
