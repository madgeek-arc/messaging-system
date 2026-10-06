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
