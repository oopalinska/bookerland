package pl.oopalinska.bookerland.catalog.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.domain.Book;

import java.util.List;

@RequestMapping("/catalog")
@RestController
@AllArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    public List<Book> getAll() {
        return catalog.findAll();
    }
    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return catalog.findById(id).orElse(null);
    }
}
