package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.Correspondent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class UnreadMessages {

    private int totalUnread;
    private List<GroupUnreadMessages> groups = new ArrayList<>();

    public UnreadMessages() {
    }

    public UnreadMessages(int totalUnread, List<GroupUnreadMessages> groups) {
        this.totalUnread = totalUnread;
        this.groups = groups;
    }

    public static Mono<UnreadMessages> of(Collection<String> groups, Flux<ThreadDTO> threads) {
        Map<String, Integer> groupUnread = new TreeMap<>();
        return Mono.from(threads.map(thread -> {

                    int unreadCount = thread.getUnread() > 0 ? 1 : 0;

                    for (Correspondent correspondent : thread.getTo()) {
                        if (groups.contains(correspondent.getGroupId())) {
                            groupUnread.putIfAbsent(correspondent.getGroupId(), 0);
                            groupUnread.put(correspondent.getGroupId(), groupUnread.get(correspondent.getGroupId()) + unreadCount);
                        }
                    }
                    return groupUnread;
                })
                .collect(TreeMap::new, Map::putAll)
                .map(treeMap -> {
                    UnreadMessages unread = new UnreadMessages();
                    unread.setTotalUnread(treeMap
                            .values()
                            .stream()
                            .map(o -> (Integer) o)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .sum());

                    unread.setGroups(
                            treeMap
                                    .entrySet()
                                    .stream()
                                    .map(entry -> GroupUnreadMessages.of((String) entry.getKey(), (Integer) entry.getValue()))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList()));
                    return unread;
                }));

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
