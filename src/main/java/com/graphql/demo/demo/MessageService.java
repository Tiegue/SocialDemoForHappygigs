package com.graphql.demo.demo;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
class MessageService {
    private final Set<Message> messages = new HashSet<>();
    private final Sinks.Many<Message> sink;

    public MessageService(Sinks.Many<Message> sink) {
        this.sink = sink;
    }

    public Flux<Message> getMessages() {
        return Flux.fromIterable(messages);
    }

    public Mono<Message> addMessage(String content) {
        Message message = new Message(content);
        messages.add(message);
        sink.tryEmitNext(message);
        return Mono.just(message);
    }

    public Flux<Message> subscribeMessages() {
        return sink.asFlux();
    }
}