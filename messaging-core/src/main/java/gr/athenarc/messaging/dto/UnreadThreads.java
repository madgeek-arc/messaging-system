package gr.athenarc.messaging.dto;

import gr.athenarc.messaging.domain.Correspondent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class UnreadThreads {

    private int totalUnread;
    private List<GroupUnread> groups = new ArrayList<>();

    public UnreadThreads() {
    }

    public UnreadThreads(int totalUnread, List<GroupUnread> groups) {
        this.totalUnread = totalUnread;
        this.groups = groups;
    }

    public static Mono<UnreadThreads> of(Collection<String> groups, Flux<ThreadDTO> threads) {
        Map<String, Integer> groupUnread = new TreeMap<>();
        return Mono.from(threads.map(thread -> {

                    if (!thread.isRead()) {
                        // increase unread count for each correspondent (group) of the thread's last message
                        for (Correspondent correspondent : thread.getMessages().get(thread.getMessages().size()-1).getTo()) {
                            if (groups.contains(correspondent.getGroupId())) {
                                groupUnread.putIfAbsent(correspondent.getGroupId(), 0);
                                groupUnread.put(correspondent.getGroupId(), groupUnread.get(correspondent.getGroupId()) + 1);
                            }
                        }
                    }
                    return groupUnread;
                })
                .collect(TreeMap::new, Map::putAll)
                .map(treeMap -> {
                    UnreadThreads unread = new UnreadThreads();
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
                                    .map(entry -> GroupUnread.of((String) entry.getKey(), (Integer) entry.getValue()))
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

    public List<GroupUnread> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupUnread> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnreadThreads that = (UnreadThreads) o;
        return totalUnread == that.totalUnread && Objects.equals(groups, that.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalUnread, groups);
    }
}
