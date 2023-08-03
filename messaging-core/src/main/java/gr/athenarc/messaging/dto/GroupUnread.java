package gr.athenarc.messaging.dto;

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
}
