package gr.athenarc.messaging.domain;

public class UserGroup extends Correspondent {

    private String groupId;

    public UserGroup() {
        // no-arg constructor
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
