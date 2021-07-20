package pl.oopalinska.bookerland.order.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import pl.oopalinska.bookerland.catalog.application.port.CatalogUseCase;
import pl.oopalinska.bookerland.catalog.db.BookJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.Delivery;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    BookJpaRepository bookRepository;
    @Autowired
    ManipulateOrderService service;
    @Autowired
    QueryOrderUseCase queryOrderService;
    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void userCanPlaceOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        Book jcip = givenJavaConcurrency(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 15))
                .item(new OrderItemCommand(jcip.getId(), 10))
                .build();
        //when
        PlaceOrderResponse response = service.placeOrder(command);
        //then
        assertTrue(response.isSuccess());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(40L, availableCopiesOf(jcip));

    }
    @Test
    public void userCanRevokeOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        User user = user("marek@example.org");
        Long orderId = placedOrder(effectiveJava.getId(), 15, user.getUsername());
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user);
        service.updateOrderStatus(command);
        //then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }
    @Test
    public void userCannotRevokePaidOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        User user = user("marek@example.org");
        Long orderId = placedOrder(effectiveJava.getId(), 15, user.getUsername());
        UpdateStatusCommand payCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, user);
        service.updateOrderStatus(payCommand);
        //when
        UpdateStatusCommand cancelCommand = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(cancelCommand);
        });
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertTrue(exception.getMessage().contains("Unable to mark"));
    }
    @Test
    public void userCannotRevokeShippedOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        User user = user("marek@example.org");
        Long orderId = placedOrder(effectiveJava.getId(), 15, user.getUsername());
        UpdateStatusCommand payCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, user);
        service.updateOrderStatus(payCommand);
        UpdateStatusCommand shipCommand = new UpdateStatusCommand(orderId, OrderStatus.SHIPPED, user);
        service.updateOrderStatus(shipCommand);
        //when
        UpdateStatusCommand cancelCommand = new UpdateStatusCommand(orderId, OrderStatus.PAID, user);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateOrderStatus(cancelCommand);
        });
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertTrue(exception.getMessage().contains("Unable to mark"));
    }
    @Test
    public void userCannotOrderNotExistingBooks() {
        //given
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(-1L, 15))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertEquals("The book with id: " + command.getItems().get(0).getBookId() + " does not exist in our repository.", exception.getMessage());
    }
    @Test
    public void userCannotOrderNegativeNumberOfBooks() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), -15))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertEquals("Quantity cannot be negative!", exception.getMessage());
    }
    @Test
    public void userCannotOrderMoreBooksThanAvailable() {
        //given
        Book effectiveJava = givenEffectiveJava(5L);
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient())
                .item(new OrderItemCommand(effectiveJava.getId(), 10))
                .build();
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.placeOrder(command);
        });
        //then
        assertTrue(exception.getMessage().contains("Too many copies of book " + effectiveJava.getId() + " requested"));

    }
    @Test
    public void userCannotRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String email = "adam@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, email);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, user("marek@example.org"));
        service.updateOrderStatus(command);
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.NEW, queryOrderService.findById(orderId).get().getStatus());
    }
    @Test
    public void adminCanRevokeOtherUsersOrder() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String userEmail = "adam@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, userEmail);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        String adminEmail = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.CANCELED, adminUser());
        service.updateOrderStatus(command);
        //then
        assertEquals(50L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.CANCELED, queryOrderService.findById(orderId).get().getStatus());
    }
    @Test
    public void adminCanMarkOrderAsPaid() {
        //given
        Book effectiveJava = givenEffectiveJava(50L);
        String email = "marek@example.org";
        Long orderId = placedOrder(effectiveJava.getId(), 15, email);
        assertEquals(35L, availableCopiesOf(effectiveJava));
        //when
        String adminEmail = "admin@example.org";
        UpdateStatusCommand command = new UpdateStatusCommand(orderId, OrderStatus.PAID, adminUser());
        service.updateOrderStatus(command);
        //then
        assertEquals(35L, availableCopiesOf(effectiveJava));
        assertEquals(OrderStatus.PAID, queryOrderService.findById(orderId).get().getStatus());
    }
    @Test
    public void shippingCostsAreAddedToTotalOrderPrice() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placedOrder(book.getId(), 1);
        //then
        assertEquals("59.80", orderOf(orderId).getFinalPrice().toPlainString());
    }
    @Test
    public void shippingCostsAreDiscountedOver100zl() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placedOrder(book.getId(), 3);
        //then
        RichOrder order = orderOf(orderId);
        assertEquals("149.70", order.getFinalPrice().toPlainString());
        assertEquals("149.70", order.getOrderPrice().getItemsPrice().toPlainString());
    }
    @Test
    public void cheapestBookIsHalfPricedWhenTotalOver200zl() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placedOrder(book.getId(), 5);
        //then
        assertEquals("224.55", orderOf(orderId).getFinalPrice().toPlainString());
    }
    @Test
    public void cheapestBookIsFreeWhenTotalOver400zl() {
        //given
        Book book = givenBook(50L, "49.90");
        //when
        Long orderId = placedOrder(book.getId(), 10);
        //then
        assertEquals("449.10", orderOf(orderId).getFinalPrice().toPlainString());
    }

    private Long placedOrder(Long bookId, int copies, String recipient) {
        PlaceOrderCommand command = PlaceOrderCommand
                .builder()
                .recipient(recipient(recipient))
                .item(new OrderItemCommand(bookId, copies))
                .delivery(Delivery.COURIER)
                .build();
        return service.placeOrder(command).getRight();
    }
    private Long placedOrder(Long bookId, int copies) {
        return placedOrder(bookId, copies, "john@example.org");
    }
    private RichOrder orderOf(Long orderId) {
        return queryOrderService.findById(orderId).get();
    }
    private Book givenBook(long available, String price) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal(price), available));
    }
    private Book givenJavaConcurrency(long available) {
        return bookRepository.save(new Book("Java Concurrency in Practice", 2006, new BigDecimal("99.90"), available));
    }
    private Book givenEffectiveJava(long available) {
        return bookRepository.save(new Book("Effective Java", 2005, new BigDecimal("199.90"), available));
    }
    private User user(String email) {
        return new User(email, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
    private User adminUser() {
        return new User("admin", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
    private Recipient recipient() {
        return Recipient.builder().email("john@example.org").build();
    }
    private Recipient recipient(String email) {
        return Recipient.builder().email(email).build();
    }
    private Long availableCopiesOf(Book book) {
        return catalogUseCase.findById(book.getId()).get().getAvailable();
    }
}