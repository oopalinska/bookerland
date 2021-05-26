package pl.oopalinska.bookerland.catalog.application.port;

import pl.oopalinska.bookerland.catalog.domain.Author;

import java.util.List;

public interface AuthorsUseCase {
    List<Author> findAll();
}
