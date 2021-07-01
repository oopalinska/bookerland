package pl.oopalinska.bookerland.catalog.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.db.AuthorJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Author;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.*;

@SpringBootTest
@AutoConfigureTestDatabase
class CatalogControllerIT {
    @Autowired
    AuthorJpaRepository authorJpaRepository;
    @Autowired
    CatalogUseCase catalogUseCase;
    @Autowired
    CatalogController controller;

    @Test
    public void getAllBooks() {
        //given
        Author goetz = authorJpaRepository.save(new Author("Brian Goetz"));
        Author bloch = authorJpaRepository.save(new Author("Joshua Bloch"));
        Book book1 = catalogUseCase.addBook(new CreateBookCommand(
                "Effective Java",
                Set.of(bloch.getId()),
                2005,
                new BigDecimal("99.90"),
                50L
        ));
        Book book2 = catalogUseCase.addBook(new CreateBookCommand(
                "Java Concurrency in Practice",
                Set.of(goetz.getId()),
                2006,
                new BigDecimal("129.90"),
                50L
        ));
        //when
        List<Book> all = controller.getAll(Optional.empty(), Optional.empty());
        //then
        assertEquals(2, all.size());
    }
}