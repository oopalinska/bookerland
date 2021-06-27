package pl.oopalinska.bookerland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.oopalinska.bookerland.order.application.OrdersProperties;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(OrdersProperties.class)
public class BookerlandOnlineBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookerlandOnlineBookstoreApplication.class, args);
	}
}

