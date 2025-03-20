package socialdemo.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphqlWebfluxApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraphqlWebfluxApplication.class, args);
	}

//	@Bean
//	public Sinks.Many<Message> messageSink() {
//		return Sinks.many().multicast().onBackpressureBuffer();
//	}
}
