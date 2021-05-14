package pl.oopalinska.bookerland;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.UpdateBookCommand;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.UpdateBookResponse;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase manipulateOrderService;
    private final QueryOrderUseCase queryOrderService;
    private final String title;
    private final String author;
    private final Long limit;

    public ApplicationStartup(
            CatalogUseCase catalog,
            ManipulateOrderUseCase manipulateOrderService,
            QueryOrderUseCase queryOrderService,
            @Value("${bookerland.catalog.title}") String title,
            @Value("${bookerland.catalog.author}") String author,
            @Value("${bookerland.catalog.limit}") Long limit
    ) {
            this.catalog = catalog;
            this.manipulateOrderService = manipulateOrderService;
            this.queryOrderService = queryOrderService;
            this.title = title;
            this.author = author;
            this.limit = limit;
    }

    @Override
    public void run(String... args) {
        initData();
        searchCatalog();
        placeOrder();
    }

    private void placeOrder() {
        Book panTadeusz = catalog
                .findOneByTitle("Pan Tadeusz")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book chlopi = catalog
                .findOneByTitle("Chłopi")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));

        Recipient recipient = Recipient.builder()
                .name("Janusz Przykładowy")
                .phone("883256134")
                .street("Plac Kościuszki 11/125")
                .city("Wrocław")
                .zipCode("40-245")
                .email("janusz@kow.pl")
                .build();

        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient)
                .item(new OrderItem(panTadeusz.getId(), 16))
                .item(new OrderItem(chlopi.getId(), 7))
                .build();

        PlaceOrderResponse response = manipulateOrderService.placeOrder((command));
        System.out.println("Created ORDER with id: " + response.getOrderId());

        queryOrderService.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    private void searchCatalog() {
        findByTitle();
        findAndUpdate();
        findByTitle();
    }

    private void initData() {
        catalog.addBook(new CreateBookCommand("Atlas Chmur", "David Mitchell", 2008, new BigDecimal("19.90")));
        catalog.addBook(new CreateBookCommand("Harry Potter i Więzień Azkabanu", "J.K. Rowling", 2005, new BigDecimal("29.90")));
        catalog.addBook(new CreateBookCommand("Zmierzch", "Stephenie Meyer", 2007, new BigDecimal("15.90")));
        catalog.addBook(new CreateBookCommand("Gra w Klasy", "Julio Cortazar", 2001, new BigDecimal("35.90")));
        catalog.addBook(new CreateBookCommand("Cień Wiatru", "Carlos Ruiz Zafon", 2002, new BigDecimal("27.90")));
        catalog.addBook(new CreateBookCommand("Pan Tadeusz", "Adam Mickiewicz", 1834, new BigDecimal("15.90")));
        catalog.addBook(new CreateBookCommand("Ogniem i Mieczem", "Henryk Sienkiewicz", 1884, new BigDecimal("19.90")));
        catalog.addBook(new CreateBookCommand("Chłopi", "Stanisław Reymont", 1904, new BigDecimal("13.90")));
        catalog.addBook(new CreateBookCommand("Pan Wołodyjowski", "Henryk Sienkiewicz", 1854, new BigDecimal("17.90")));
    }

    private void findByTitle() {
        List<Book> booksTitle = catalog.findByTitle(title);
        booksTitle.stream().limit(limit).forEach(System.out::println);
    }

    private void findAndUpdate() {
        System.out.println("Updating book...");
        catalog.findOneByTitleAndAuthor("Pan Tadeusz", "Adam Mickiewicz")
                .ifPresent(book -> {
                    UpdateBookCommand command = UpdateBookCommand
                            .builder()
                            .id(book.getId())
                            .title("Pan Tadeusz, czyli Ostatni Zajazd na Litwie")
                            .build();
                    UpdateBookResponse response = catalog.updateBook(command);
                    System.out.println("Updating book result: " + response.isSuccess());
                });
    }
}
