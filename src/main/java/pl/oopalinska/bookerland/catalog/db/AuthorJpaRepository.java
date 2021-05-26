package pl.oopalinska.bookerland.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Author;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {
}
