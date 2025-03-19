package com.graphql.demo.demo;


import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @QueryMapping
    public Flux<Message> messages() {
        return messageService.getMessages();
    }

    @MutationMapping
    public Mono<Message> addMessage(@Argument String content) {
        return messageService.addMessage(content);
    }

    @SubscriptionMapping
    public Flux<Message> messageSubscription() {
        return messageService.subscribeMessages();
    }
}