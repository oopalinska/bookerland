package pl.oopalinska.bookerland;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.oopalinska.bookerland.catalog.application.CatalogController;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.catalog.domain.CatalogRepository;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class BookerlandOnlineBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookerlandOnlineBookstoreApplication.class, args);
	}
}
