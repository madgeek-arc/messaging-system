package gr.athenarc.messaging.domain;

import java.util.Set;
import java.util.TreeSet;

public class Metadata {

    private Correspondent sentBy;
    private boolean anonymousSender;
    private Set<String> readBy = new TreeSet<>();

    public Metadata() {
        // no-arg constructor
    }

    public Metadata(Correspondent sentBy, boolean anonymousSender) {
        this.sentBy = sentBy;
        this.anonymousSender = anonymousSender;
    }

    public Metadata(Correspondent sentBy, boolean anonymousSender, Set<String> readBy) {
        this.sentBy = sentBy;
        this.anonymousSender = anonymousSender;
        this.readBy = readBy;
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

    public Set<String> getReadBy() {
        return readBy;
    }

    public void setReadBy(Set<String> readBy) {
        this.readBy = readBy;
    }
}
