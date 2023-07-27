package gr.athenarc.messaging.dto;

public class GroupUnreadMessages {

    private String groupId;
    private int unread = 0;

    public GroupUnreadMessages() {
    }

    public GroupUnreadMessages(String groupId, int unread) {
        this.groupId = groupId;
        this.unread = unread;
    }

    public static GroupUnreadMessages of(String groupId, int unread) {
        GroupUnreadMessages group = new GroupUnreadMessages();
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
