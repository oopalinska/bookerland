package pl.oopalinska.bookerland.catalog.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    @Query(" SELECT DISTINCT b FROM Book b JOIN FETCH b.authors ")
    List<Book> findAllEager();

    List<Book> findByAuthors_firstNameContainsIgnoreCaseOrAuthors_lastNameContainsIgnoreCase(String name, String lastName);

    List<Book> findByTitleContainsIgnoreCase(String title);

    Optional<Book> findFirstByTitleContainsIgnoreCase(String title);

    @Query(
            " SELECT b FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " lower(a.firstName) LIKE lower(concat('%', :name, '%')) " +
                    " OR lower(a.lastName) LIKE lower(concat('%', :name, '%')) "
    )
    List<Book> findByAuthor(@Param("name") String name);

    @Query(
            " SELECT b FROM Book b JOIN b.authors a " +
                    " WHERE " +
                    " lower(b.title) LIKE lower(concat('%', :title, '%')) " +
                    " AND (lower(a.firstName) LIKE lower(concat('%', :author, '%')) " +
                    " OR lower(a.lastName) LIKE lower(concat('%', :author, '%'))) "
    )
    List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);
}
