package pl.oopalinska.bookerland;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase.CreateBookCommand;
import pl.oopalinska.bookerland.catalog.db.AuthorJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Author;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderResponse;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.Set;

@Component
@AllArgsConstructor
public class ApplicationStartup implements CommandLineRunner {
    private final CatalogUseCase catalog;
    private final ManipulateOrderUseCase manipulateOrderService;
    private final QueryOrderUseCase queryOrderService;
    private final AuthorJpaRepository authorJpaRepository;

    @Override
    public void run(String... args) {
        initData();
        placeOrder();
    }

    private void placeOrder() {
        Book effectiveJava = catalog
                .findOneByTitle("Effective Java")
                .orElseThrow(() -> new IllegalStateException("Cannot find a book"));
        Book puzzlers = catalog
                .findOneByTitle("Java Puzzlers")
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
                .item(new OrderItem(effectiveJava.getId(), 16))
                .item(new OrderItem(puzzlers.getId(), 7))
                .build();

        PlaceOrderResponse response = manipulateOrderService.placeOrder((command));
        System.out.println("Created ORDER with id: " + response.getOrderId());

        queryOrderService.findAll()
                .forEach(order -> System.out.println("GOT ORDER WITH TOTAL PRICE: " + order.totalPrice() + " DETAILS: " + order));
    }

    private void initData() {
        Author joshua = new Author("Joshua", "Bloch");
        Author neal = new Author("Neal", "Gafter");
        authorJpaRepository.save(joshua);
        authorJpaRepository.save(neal);

        CreateBookCommand effectiveJava = new CreateBookCommand("Effective Java",
                Set.of(joshua.getId()),
                2005,
                new BigDecimal("79.90"));
        CreateBookCommand javaPuzzlers = new CreateBookCommand("Java Puzzlers",
                Set.of(joshua.getId(), neal.getId()),
                2018,
                new BigDecimal("99.90"));
       catalog.addBook(effectiveJava);
       catalog.addBook(javaPuzzlers);
    }
}
