package pl.oopalinska.bookerland;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BookerlandOnlineBookstoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BookerlandOnlineBookstoreApplication.class, args);
	}

	private final CatalogService catalogService;

	public BookerlandOnlineBookstoreApplication(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public void run(String... args) throws Exception {
		List<Book> books = catalogService.findByTitle("Pan Tadeusz");
		books.forEach(System.out::println);
	}
}
