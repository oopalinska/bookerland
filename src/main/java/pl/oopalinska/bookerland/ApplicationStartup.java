package pl.oopalinska.bookerland;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.oopalinska.bookerland.catalog.domain.Book;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogUseCase catalog;
    private final String title;
    private final String author;
    private final Long limit;

    public ApplicationStartup(
            CatalogUseCase catalog,
            @Value("${bookerland.catalog.title}") String title,
            @Value("${bookerland.catalog.author}") String author,
            @Value("${bookerland.catalog.limit}") Long limit
    ) {
            this.catalog = catalog;
            this.title = title;
            this.author = author;
            this.limit = limit;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CreateBookCommand("Atlas Chmur", "David Mitchell", 2008));
        catalog.addBook(new CreateBookCommand("Harry Potter i Więzień Azkabanu", "J.K. Rowling", 2005));
        catalog.addBook(new CreateBookCommand("Zmierzch", "Stephenie Meyer", 2007));
        catalog.addBook(new CreateBookCommand("Gra w Klasy", "Julio Cortazar", 2001));
        catalog.addBook(new CreateBookCommand("Cień Wiatru", "Carlos Ruiz Zafon", 2002));
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884));
        catalog.addBook(new CreateBookCommand("Chłopi", "Stanisław Reymont", 1904));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1854));
    }

    private void findByTitle() {
        List<Book> booksTitle = catalog.findByTitle(title);
        booksTitle.stream().limit(limit).forEach(System.out::println);
    }

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand command = UpdateBookCommand.builder()
                            .id(book.getId())
                            .title("Pan Tadeusz, czyli Ostatni Zajazd na Litwie")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " + response.isSuccess());
                });
    }
}
