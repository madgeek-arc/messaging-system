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
