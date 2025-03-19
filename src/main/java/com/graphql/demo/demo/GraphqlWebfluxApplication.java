package com.graphql.demo.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class GraphqlWebfluxApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraphqlWebfluxApplication.class, args);
	}

	@Bean
	public Sinks.Many<Message> messageSink() {
		return Sinks.many().multicast().onBackpressureBuffer();
	}
}
