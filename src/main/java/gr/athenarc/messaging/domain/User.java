package gr.athenarc.messaging.domain;

public class User extends Correspondent {

    private String email;

    public User() {
        // no-arg constructor
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
