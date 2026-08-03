package gr.athenarc.messaging.dto;

/**
 * Identifies the user whose personal data is to be erased.
 * <p>
 * The email travels in a request body rather than a query parameter on purpose: query strings are
 * recorded by reverse proxies and access logs by default, which would spread the very address the
 * request exists to erase.
 */
public class AnonymizeUserRequest {

    private String email;

    public AnonymizeUserRequest() {
        // no-arg constructor
    }

    public AnonymizeUserRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
