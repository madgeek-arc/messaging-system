package gr.athenarc.messaging.service;

import reactor.core.publisher.Mono;

public interface CrudOperations <T, ID> {

    Mono<T> get(ID id);
    Mono<T> add(T t);
    Mono<T> update(ID id, T t);
    Mono<Void> delete(ID id);
}
