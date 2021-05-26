package pl.oopalinska.bookerland.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.catalog.application.port.AuthorsUseCase;
import pl.oopalinska.bookerland.catalog.db.AuthorJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Author;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorsService implements AuthorsUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }
}
