package gr.athenarc.messaging.domain;

import java.util.Date;

public class StoredMessage {

    private String id;
    private Message message;
    private Metadata metadata;

    public StoredMessage() {
        // no-arg constructor
    }

    public static StoredMessage of(final Message message, final boolean anonymousSender) {
        StoredMessage storedMessage = new StoredMessage();

        Metadata metadata = new Metadata();
        metadata.setSentBy(message.getFrom());
        metadata.setAnonymousSender(anonymousSender);

        if (message.getDate() == null) {
            message.setDate(new Date());
        }

        storedMessage.setMessage(message);
        storedMessage.setMetadata(metadata);
        return storedMessage;
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
