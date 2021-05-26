package pl.oopalinska.bookerland.catalog.application.port;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CatalogUseCase {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    List<Book> findByTitle(String title);

    Optional<Book> findOneByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByTitleAndAuthor(String title, String author);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    Book addBook(CreateBookCommand command);

    void removeById(Long id);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void updateBookCover(UpdateBookCoverCommand command);

    void removeBookCover(Long id);

    @Value
    class UpdateBookCoverCommand {
        Long id;
        byte[] file;
        String contentType;
        String fileName;
    }

    @Value
    class CreateBookCommand {
        String title;
        Set<Long> authors;
        Integer year;
        BigDecimal price;
    }

    @Value
    @Builder
    @AllArgsConstructor
    class UpdateBookCommand {
        Long id;
        String title;
        Set<Long> authors;
        Integer year;
        BigDecimal price;
    }

    @Value
    class UpdateBookResponse {
        boolean success;
        List<String> errors;

        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, Collections.emptyList());
    }
}
