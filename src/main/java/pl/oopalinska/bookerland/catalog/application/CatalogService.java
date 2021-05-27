package pl.oopalinska.bookerland.catalog.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.db.AuthorJpaRepository;
import pl.oopalinska.bookerland.catalog.db.BookJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Author;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.uploads.application.ports.UploadUseCase;
import pl.oopalinska.bookerland.uploads.application.ports.UploadUseCase.SaveUploadCommand;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class CatalogService implements CatalogUseCase {
    private final BookJpaRepository repository;
    private final AuthorJpaRepository authorRepository;
    private final UploadUseCase upload;

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findByTitleStartsWithIgnoreCase(title);
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return repository.findAll()
                         .stream()
                         .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                         .findFirst();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findByAuthor(author);
    }
    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                         .stream()
//                         .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                         .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                         .collect(Collectors.toList());
    }

    @Override
    public Book addBook(CreateBookCommand command) {
        Book book = toBook(command);
        return repository.save(book);
    }
    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(), command.getYear(), command.getPrice());
        Set<Author> authors = fetchAuthorsByIds(command.getAuthors());
        book.setAuthors(authors);
        return book;
    }

    private Set<Author> fetchAuthorsByIds(Set<Long> authors) {
        return authors
                .stream()
                .map(authorId -> authorRepository
                        .findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException(("Unable to find author with id: " + authorId)))
                )
                .collect(Collectors.toSet());
    }

    @Override
    public void removeById(Long id){
        repository.deleteById(id);
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository
                .findById(command.getId())
                .map(book -> {
                    var updatedBook = updateFields(command, book);
                    repository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateBookResponse(false, Collections.singletonList("Book not found with id: " + command.getId())));
    }
    private Book updateFields(UpdateBookCommand command, Book book) {
            if (command.getTitle() != null) {
                book.setTitle(command.getTitle());
            }
            if (command.getAuthors() != null && command.getAuthors().size() > 0) {
                book.setAuthors(fetchAuthorsByIds(command.getAuthors()));
            }
            if (command.getYear() != null) {
                book.setYear(command.getYear());
            }
            if (command.getPrice() != null) {
                book.setPrice(command.getPrice());
            }
            return book;
        }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        repository.findById(command.getId())
                  .ifPresent(book -> {
                      var savedUpload = upload.save(new SaveUploadCommand(command.getFileName(), command.getFile(), command.getContentType()));
                      book.setCoverId(savedUpload.getId());
                      repository.save(book);
                  });
    }

    @Override
    public void removeBookCover(Long id) {
        repository.findById(id)
                .ifPresent(book -> {
                    if(book.getCoverId() != null) {
                        upload.removeById(book.getCoverId());
                        book.setCoverId(null);
                        repository.save(book);
                    }
                });
    }
}
