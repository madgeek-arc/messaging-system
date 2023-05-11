package gr.athenarc.messaging.domain;

public class Correspondent {

    private String name;
    private String email;
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
