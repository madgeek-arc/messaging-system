package gr.athenarc.messaging.privacy;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.Metadata;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static gr.athenarc.messaging.privacy.PersonalDataEraser.DELETED_USER_PLACEHOLDER;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalDataEraserTest {

    private static final String PURGED = "jane@example.org";
    private static final String OTHER = "bob@example.org";

    @Test
    void erasesAllFiveCorrespondentSlots() {
        TopicThread thread = thread();

        assertTrue(PersonalDataEraser.anonymize(thread, PURGED));

        assertAnonymized(thread.getFrom());
        assertAnonymized(thread.getTo().get(0));
        Message message = thread.getMessages().get(0).getMessage();
        assertAnonymized(message.getFrom());
        assertAnonymized(message.getTo().get(0));
        assertAnonymized(thread.getMessages().get(0).getMetadata().getSentBy());
    }

    @Test
    void dropsTheReadReceipt() {
        TopicThread thread = thread();

        PersonalDataEraser.anonymize(thread, PURGED);

        Set<String> readBy = thread.getMessages().get(0).getMetadata().getReadBy();
        assertFalse(readBy.contains(PURGED));
        assertTrue(readBy.contains(OTHER), "other users' read receipts must survive");
    }

    @Test
    void leavesOtherCorrespondentsAlone() {
        TopicThread thread = thread();

        PersonalDataEraser.anonymize(thread, PURGED);

        assertEquals("Bob", thread.getTo().get(1).getName());
        assertEquals(OTHER, thread.getTo().get(1).getEmail());
    }

    @Test
    void leavesProseAndNonPersonalFieldsAlone() {
        TopicThread thread = thread();

        PersonalDataEraser.anonymize(thread, PURGED);

        assertEquals("A subject", thread.getSubject());
        assertEquals("hello there", thread.getMessages().get(0).getMessage().getBody());
        assertEquals(List.of("tag-a"), thread.getTags());
        assertEquals("group-1", thread.getFrom().getGroupId());
        assertTrue(thread.getMessages().get(0).getMetadata().isAnonymousSender(),
                "anonymousSender records a sender's own choice, not erasure");
    }

    @Test
    void isIdempotent() {
        TopicThread thread = thread();

        assertTrue(PersonalDataEraser.anonymize(thread, PURGED));
        assertFalse(PersonalDataEraser.anonymize(thread, PURGED), "second run must report no change");

        assertAnonymized(thread.getFrom());
        assertEquals(1, thread.getMessages().get(0).getMetadata().getReadBy().size());
    }

    @Test
    void reportsNoChangeForAnAbsentUser() {
        TopicThread thread = thread();

        assertFalse(PersonalDataEraser.anonymize(thread, "nobody@example.org"));

        assertEquals("Jane", thread.getFrom().getName());
    }

    @Test
    void matchesEmailIgnoringCase() {
        TopicThread thread = thread();

        assertTrue(PersonalDataEraser.anonymize(thread, "JANE@Example.ORG"));

        assertAnonymized(thread.getFrom());
        assertFalse(thread.getMessages().get(0).getMetadata().getReadBy().contains(PURGED));
    }

    @Test
    void handlesTheAliasedSentByReference() {
        // StoredMessage#of assigns metadata.sentBy = message.from, i.e. the very same object.
        Message message = new Message(correspondent("Jane", PURGED), new ArrayList<>(), "hi", new Date(), null);
        StoredMessage storedMessage = StoredMessage.of(message, false);
        storedMessage.setId("0");
        TopicThread thread = new TopicThread();
        thread.setMessages(new ArrayList<>(List.of(storedMessage)));

        assertTrue(PersonalDataEraser.anonymize(thread, PURGED));

        assertAnonymized(storedMessage.getMessage().getFrom());
        assertAnonymized(storedMessage.getMetadata().getSentBy());
    }

    @Test
    void toleratesNullsAndBlanks() {
        assertDoesNotThrow(() -> {
            assertFalse(PersonalDataEraser.anonymize(null, PURGED));
            assertFalse(PersonalDataEraser.anonymize(new TopicThread(), null));
            assertFalse(PersonalDataEraser.anonymize(new TopicThread(), " "));
            // a bare thread: null from, null to, null messages
            assertFalse(PersonalDataEraser.anonymize(new TopicThread(), PURGED));
        });
    }

    @Test
    void toleratesNullElementsInsideTheDocument() {
        TopicThread thread = new TopicThread();
        thread.setFrom(correspondent("Jane", PURGED));
        thread.setTo(new ArrayList<>(Arrays.asList(null, correspondent("Jane", PURGED))));

        StoredMessage withoutMessage = new StoredMessage();
        withoutMessage.setId("0");
        Message messageWithoutRecipients = new Message();
        messageWithoutRecipients.setFrom(correspondent("Jane", PURGED));
        StoredMessage withoutMetadata = new StoredMessage();
        withoutMetadata.setId("1");
        withoutMetadata.setMessage(messageWithoutRecipients);
        thread.setMessages(new ArrayList<>(Arrays.asList(null, withoutMessage, withoutMetadata)));

        assertDoesNotThrow(() -> assertTrue(PersonalDataEraser.anonymize(thread, PURGED)));

        assertAnonymized(thread.getFrom());
        assertAnonymized(thread.getTo().get(1));
        assertAnonymized(messageWithoutRecipients.getFrom());
    }

    private static void assertAnonymized(Correspondent correspondent) {
        assertEquals(DELETED_USER_PLACEHOLDER, correspondent.getName());
        assertEquals(DELETED_USER_PLACEHOLDER, correspondent.getEmail());
    }

    private static Correspondent correspondent(String name, String email) {
        return new Correspondent(name, email, "group-1");
    }

    /**
     * A thread carrying the purged user in all five correspondent slots and in readBy, alongside
     * a second user who must come through untouched.
     */
    private static TopicThread thread() {
        TopicThread thread = new TopicThread();
        thread.setId("thread-1");
        thread.setSubject("A subject");
        thread.setTags(new ArrayList<>(List.of("tag-a")));
        thread.setFrom(correspondent("Jane", PURGED));
        thread.setTo(new ArrayList<>(List.of(correspondent("Jane", PURGED), correspondent("Bob", OTHER))));

        Message message = new Message(
                correspondent("Jane", PURGED),
                new ArrayList<>(List.of(correspondent("Jane", PURGED), correspondent("Bob", OTHER))),
                "hello there",
                new Date(),
                null);

        // Mongo round-trips sentBy into a distinct object, unlike StoredMessage#of.
        Metadata metadata = new Metadata(correspondent("Jane", PURGED), true,
                new TreeSet<>(List.of(PURGED, OTHER)));

        StoredMessage storedMessage = new StoredMessage();
        storedMessage.setId("0");
        storedMessage.setMessage(message);
        storedMessage.setMetadata(metadata);

        thread.setMessages(new ArrayList<>(List.of(storedMessage)));
        thread.setCreated(new Date());
        thread.setUpdated(new Date());
        return thread;
    }
}
