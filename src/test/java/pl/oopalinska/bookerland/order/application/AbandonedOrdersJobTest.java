package pl.oopalinska.bookerland.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.db.BookJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.clock.Clock;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "app.orders.payment-period=1H"
)
@AutoConfigureTestDatabase
class AbandonedOrdersJobTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Clock.Fake clock() {
            return new Clock.Fake();
        }
    }

    @Autowired
    ManipulateOrderService manipulateOrderService;
    @Autowired
    BookJpaRepository bookRepository;
    @Autowired
    QueryOrderUseCase queryOrderService;
    @Autowired
    CatalogUseCase catalogUseCase;
    @Autowired
    Clock.Fake clock;
    @Autowired
    AbandonedOrdersJob ordersJob;


    @Test
    public void shouldMarkOrdersAsAbandoned() {
        //given
        Book book = givenEffectiveJava(50L);
        Long orderId = placedOrder(book.getId(), 15);
        //when
        clock.tick(Duration.ofHours(2));
        ordersJob.run();
        //then
        assertEquals(50L, availableCopiesOf(book));
        assertEquals(OrderStatus.ABANDONED, queryOrderService.findById(orderId).get().getStatus());
    }

    private Long placedOrder(Long bookId, int copies) {
        ManipulateOrderUseCase.PlaceOrderCommand command = ManipulateOrderUseCase.PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new ManipulateOrderUseCase.OrderItemCommand(bookId, copies))
                .build();
        return manipulateOrderService.placeOrder(command).getRight();
    }
    private Recipient recipient() {
        return Recipient.builder().email("marek@example.org").build();
    }
    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }
    private Long availableCopiesOf(Book book) {
        return catalogUseCase.findById(book.getId()).get().getAvailable();
    }
}