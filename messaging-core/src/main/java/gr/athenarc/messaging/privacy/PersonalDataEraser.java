/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.messaging.privacy;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.Message;
import gr.athenarc.messaging.domain.Metadata;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;

import java.util.List;

/**
 * Erases a user's personal data from stored threads, in place.
 * <p>
 * Personal data lives in five {@link Correspondent} slots, each carrying both a name and an email:
 * {@code TopicThread.from}, {@code TopicThread.to[]}, {@code messages[].message.from},
 * {@code messages[].message.to[]} and {@code messages[].metadata.sentBy}. On top of those,
 * {@code messages[].metadata.readBy} is a set of read receipts keyed by email.
 * <p>
 * The correspondent slots are anonymised (name and email replaced with
 * {@link #DELETED_USER_PLACEHOLDER}) so that the shape of the conversation survives; the read
 * receipt is removed outright, since a receipt attributed to nobody carries no meaning.
 * <p>
 * Deliberately left untouched: {@code subject} and {@code message.body}, which are prose a third
 * party wrote; {@code groupId}, which is not personal; and {@code metadata.anonymousSender}, which
 * records that a sender <em>chose</em> anonymity - a different fact from erasure.
 */
public final class PersonalDataEraser {

    public static final String DELETED_USER_PLACEHOLDER = "[Deleted User]";

    private PersonalDataEraser() {
        // utility class
    }

    /**
     * Anonymises every trace of the given user in the given thread.
     * <p>
     * Idempotent: re-running on an already anonymised thread changes nothing and reports no change,
     * so callers can skip the write.
     *
     * @param thread the thread to anonymise, modified in place
     * @param email  the email of the user to erase, matched ignoring case
     * @return true if the thread was modified
     */
    public static boolean anonymize(TopicThread thread, String email) {
        if (thread == null || email == null || email.isBlank()) {
            return false;
        }

        boolean modified = anonymize(thread.getFrom(), email);
        modified |= anonymizeAll(thread.getTo(), email);

        if (thread.getMessages() != null) {
            for (StoredMessage storedMessage : thread.getMessages()) {
                if (storedMessage == null) {
                    continue;
                }
                modified |= anonymize(storedMessage.getMessage(), email);
                modified |= anonymize(storedMessage.getMetadata(), email);
            }
        }
        return modified;
    }

    private static boolean anonymize(Message message, String email) {
        if (message == null) {
            return false;
        }
        boolean modified = anonymize(message.getFrom(), email);
        modified |= anonymizeAll(message.getTo(), email);
        return modified;
    }

    private static boolean anonymize(Metadata metadata, String email) {
        if (metadata == null) {
            return false;
        }
        // sentBy may be the very same object as message.from (see StoredMessage#of): visiting it
        // twice is harmless, the second visit simply finds it already anonymised.
        boolean modified = anonymize(metadata.getSentBy(), email);
        if (metadata.getReadBy() != null) {
            modified |= metadata.getReadBy().removeIf(email::equalsIgnoreCase);
        }
        return modified;
    }

    private static boolean anonymizeAll(List<Correspondent> correspondents, String email) {
        if (correspondents == null) {
            return false;
        }
        boolean modified = false;
        for (Correspondent correspondent : correspondents) {
            modified |= anonymize(correspondent, email);
        }
        return modified;
    }

    private static boolean anonymize(Correspondent correspondent, String email) {
        if (correspondent == null || !email.equalsIgnoreCase(correspondent.getEmail())) {
            return false;
        }
        correspondent.setName(DELETED_USER_PLACEHOLDER);
        correspondent.setEmail(DELETED_USER_PLACEHOLDER);
        return true;
    }
}
