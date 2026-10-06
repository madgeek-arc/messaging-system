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

package gr.athenarc.messaging.dto;

import java.util.Objects;

public class GroupUnread {

    private String groupId;
    private int unread = 0;

    public GroupUnread() {
    }

    public GroupUnread(String groupId, int unread) {
        this.groupId = groupId;
        this.unread = unread;
    }

    public static GroupUnread of(String groupId, int unread) {
        GroupUnread group = new GroupUnread();
        group.setGroupId(groupId);
        group.setUnread(unread);
        if (unread <= 0) {
            return null;
        }
        return group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUnread that = (GroupUnread) o;
        return unread == that.unread && Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, unread);
    }
}
