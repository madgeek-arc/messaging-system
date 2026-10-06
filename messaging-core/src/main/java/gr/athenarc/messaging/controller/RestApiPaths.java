/*
 * Copyright 2023-2026 OpenAIRE AMKE & Athena Research and Innovation Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.athenarc.messaging.controller;

public class RestApiPaths {

    public static final String THREADS = "threads";
    public static final String THREADS_id = THREADS + "/{threadId}";
    public static final String INBOX_TOTAL_UNREAD = "inbox/unread";
    public static final String INBOX_THREADS_COUNT = "inbox/threads/count";
    public static final String INBOX_THREADS_SEARCH = "inbox/threads/search";
    public static final String INBOX_THREADS_UNREAD = "inbox/threads/unread";
    public static final String OUTBOX_THREADS_COUNT = "outbox/threads/count";
    public static final String OUTBOX_THREADS_SEARCH = "outbox/threads/search";
    public static final String THREADS_INTERNAL = THREADS + "/internal";
    public static final String THREADS_PUT_ID = THREADS + "/{threadId}";
    public static final String THREADS_id_MESSAGES = THREADS + "/{threadId}/messages";
    public static final String THREADS_id_MESSAGES_id = THREADS + "/{threadId}/messages/{messageId}";
    public static final String USER = "user";
    public static final String USER_ANONYMIZE = USER + "/anonymize";


    public static final String THREADS_FROM = THREADS + "/from";
    public static final String THREADS_TO = THREADS + "/to";
    public static final String THREADS_SEARCH = THREADS + "/search";
    public static final String THREADS_SEARCH_TAGS_SUBJECT = THREADS + "/tags";
    public static final String THREADS_GET_SUBJECT = THREADS + "/subject";
    public static final String THREADS_BY_EXAMPLE = THREADS + "/search";

    private RestApiPaths() {}
}
