package pl.oopalinska.bookerland.catalog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.catalog.domain.CatalogService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogService service;

    public List<Book> findByTitle(String title){
        return service.findByTitle(title);
    }

    public List<Book> findByAuthor(String author) {
        return service.findByAuthor(author);
    }
}
