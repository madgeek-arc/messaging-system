package gr.athenarc.messaging.controller;

public class RestApiPaths {

    public static final String THREADS = "threads";
    public static final String THREADS_id = THREADS + "/{threadId}";
    public static final String INBOX_TOTAL_UNREAD = "inbox/unread";
    public static final String INBOX_THREADS_SEARCH = "inbox/threads/search";
    public static final String INBOX_THREADS_UNREAD = "inbox/threads/unread";
    public static final String OUTBOX_THREADS_SEARCH = "outbox/threads/search";
    public static final String THREADS_INTERNAL = THREADS + "/internal";
    public static final String THREADS_PUT_ID = THREADS + "/{threadId}";
    public static final String THREADS_id_MESSAGES = THREADS + "/{threadId}/messages";
    public static final String THREADS_id_MESSAGES_id = THREADS + "/{threadId}/messages/{messageId}";


    public static final String THREADS_FROM = THREADS + "/from";
    public static final String THREADS_TO = THREADS + "/to";
    public static final String THREADS_SEARCH = THREADS + "/search";
    public static final String THREADS_SEARCH_TAGS_SUBJECT = THREADS + "/tags";
    public static final String THREADS_GET_SUBJECT = THREADS + "/subject";
    public static final String THREADS_BY_EXAMPLE = THREADS + "/search";

    private RestApiPaths() {}
}
