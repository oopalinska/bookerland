package pl.oopalinska.bookerland.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
