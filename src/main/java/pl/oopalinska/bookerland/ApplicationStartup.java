package pl.oopalinska.bookerland;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.oopalinska.bookerland.catalog.application.CatalogController;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogController catalogController;
    private final String title;
    private final String author;
    private Long limit;

    public ApplicationStartup(
            CatalogController catalogController,
            @Value("${bookerland.catalog.title}") String title,
            @Value("${bookerland.catalog.author}") String author,
            @Value("${bookerland.catalog.limit}") Long limit
    ) {
            this.catalogController = catalogController;
            this.title = title;
            this.author = author;
            this.limit = limit;
    }

    @Override
    public void run(String... args) {
        System.out.println();
        System.out.printf("Found by title: %s%n", title);
        List<Book> booksTitle = catalogController.findByTitle(title);
        booksTitle.stream().limit(limit).forEach(System.out::println);
        System.out.println("- - - - - - - - - - - ");
        System.out.printf("Found by author: %s%n", author);
        List<Book> booksAuthor = catalogController.findByAuthor(author);
        booksAuthor.stream().limit(limit).forEach(System.out::println);
    }
}
