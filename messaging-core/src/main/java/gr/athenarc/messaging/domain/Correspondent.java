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

import org.springframework.data.mongodb.core.index.Indexed;

public class Correspondent {

    private String name;
    @Indexed
    private String email;
    @Indexed
    private String groupId;

    public Correspondent() {
        // no-arg constructor
    }

    public Correspondent(String name, String email, String groupId) {
        this.name = name;
        this.email = email;
        this.groupId = groupId;
    }

    public Correspondent(final Correspondent that) {
        this(that.name, that.email, that.groupId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
