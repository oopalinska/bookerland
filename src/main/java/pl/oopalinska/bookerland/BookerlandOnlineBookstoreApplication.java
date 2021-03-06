package pl.oopalinska.bookerland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import pl.oopalinska.bookerland.order.application.OrdersProperties;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(OrdersProperties.class)
public class BookerlandOnlineBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookerlandOnlineBookstoreApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
}

