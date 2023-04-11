package gr.athenarc.messaging.domain;

public class StoredMessage {

    private Message message;
    private Metadata metadata;

    public StoredMessage() {
        // no-arg constructor
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
