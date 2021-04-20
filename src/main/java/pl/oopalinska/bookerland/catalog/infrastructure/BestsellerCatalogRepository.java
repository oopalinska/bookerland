package pl.oopalinska.bookerland.catalog.infrastructure;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.catalog.domain.CatalogRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
class BestsellerCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> storage = new ConcurrentHashMap<>();

    public BestsellerCatalogRepository() {
        storage.put(1L, new Book(1L, "Atlas Chmur", "David Mitchell", 2008));
        storage.put(2L, new Book(2L, "Harry Potter i Więzień Azkabanu", "J.K. Rowling", 2005));
        storage.put(3L, new Book(3L, "Zmierzch", "Stephenie Meyer", 2007));
        storage.put(4L, new Book(4L, "Gra w Klasy", "Julio Cortazar", 2001));
        storage.put(5L, new Book(5L, "Cień Wiatru", "Carlos Ruiz Zafon", 2002));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }
}
