package pl.oopalinska.bookerland.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.catalog.domain.CatalogRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {
    private final CatalogRepository repository;

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }
    @Override
    public List<Book> findByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return repository.findAll()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getAuthor().contains(author))
                         .collect(Collectors.toList());
    }
    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().startsWith(title))
                         .filter(book -> book.getAuthor().contains(author))
                         .findFirst();
    }
    @Override
    public void addBook(CreateBookCommand command) {
        Book book = command.toBook();
        repository.save(book);
    }
    @Override
    public void removeById(Long id){
        repository.removeById(id);
    }
    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository
                .findById(command.getId())
                .map(book -> {
                    Book updatedBook = command.updateFields(book);
                    repository.save(book);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Arrays.asList("Book not found with id: " + command.getId())));
    }
}
