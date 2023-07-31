package gr.athenarc.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {


    private UserGroups userGroups;


    public UserGroups getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(UserGroups userGroups) {
        this.userGroups = userGroups;
    }

    public static class UserGroups {
        private Endpoint groups;
        private Endpoint resolveEmails;

        public Endpoint getGroups() {
            return groups;
        }

        public void setGroups(Endpoint groups) {
            this.groups = groups;
        }

        public Endpoint getResolveEmails() {
            return resolveEmails;
        }

        public void setResolveEmails(Endpoint resolveEmails) {
            this.resolveEmails = resolveEmails;
        }
    }

    public static class Endpoint {
        private String endpoint;


        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }

}
