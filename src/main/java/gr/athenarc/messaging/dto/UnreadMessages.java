package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.Correspondent;
import gr.athenarc.messaging.domain.StoredMessage;
import gr.athenarc.messaging.domain.TopicThread;

import java.util.*;
import java.util.stream.Collectors;

public class UnreadMessages {

    private int totalUnread;
    private List<GroupUnreadMessages> groups = new ArrayList<>();

    public UnreadMessages() {
    }

    public static UnreadMessages of(Collection<String> groups, Collection<TopicThread> threads) {
        UnreadMessages unread = new UnreadMessages();
        Map<String, Integer> groupUnread = new TreeMap<>();
        for (TopicThread thread : threads) {

            int unreadCount = 0;
            for (StoredMessage storedMessage : thread.getMessages()) {
                if (!storedMessage.getMetadata().isRead()) {
                    unreadCount++;
                }
            }
            unread.totalUnread += unreadCount;
            for (Correspondent correspondent : thread.getTo()) {
                if (groups.contains(correspondent.getGroupId())) {
                    groupUnread.putIfAbsent(correspondent.getGroupId(), 0);
                    groupUnread.put(correspondent.getGroupId(), groupUnread.get(correspondent.getGroupId()) + unreadCount);
                }
            }
        }
        unread.setGroups(
                groupUnread
                        .entrySet()
                        .stream()
                        .map(entry -> GroupUnreadMessages.of(entry.getKey(), entry.getValue()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
        return unread;
    }

    public int getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(int totalUnread) {
        this.totalUnread = totalUnread;
    }

    public List<GroupUnreadMessages> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupUnreadMessages> groups) {
        this.groups = groups;
    }
}
