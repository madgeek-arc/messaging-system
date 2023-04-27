package gr.athenarc.messaging.domain;

public class StoredMessage {

    private String id;
    private Message message;
    private Metadata metadata;

    public StoredMessage() {
        // no-arg constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
