package pl.oopalinska.bookerland;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
//	CatalogRepository catalogRepository() {
//		Random random = new Random();
//		if(random.nextBoolean()) {
//			System.out.println("Wybieram szkolne lektury");
//			return new SchoolCatalogRepository();
//		} else {
//			System.out.println("Wybieram bestsellery");
//			return new BestsellerCatalogRepository();
//		}
//	}
}
