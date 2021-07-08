package pl.oopalinska.bookerland.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.oopalinska.bookerland.catalog.db.BookJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.db.OrderJpaRepository;
import pl.oopalinska.bookerland.order.db.RecipientJpaRepository;
import pl.oopalinska.bookerland.order.domain.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    private final OrderJpaRepository repository;
    private final BookJpaRepository bookJpaRepository;
    private final RecipientJpaRepository recipientJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        final Set<OrderItem> items = command.getItems()
                                            .stream()
                                            .map(this::toOrderItem)
                                            .collect(Collectors.toSet());
        Order order = Order
                .builder()
                .recipient(getOrCreateRecipient(command.getRecipient()))
                .items(items)
                .build();
        Order savedOrder = repository.save(order);
        bookJpaRepository.saveAll(reduceBooks(items));
        return PlaceOrderResponse.success(savedOrder.getId());
    }

    private Recipient getOrCreateRecipient(Recipient recipient) {
        return recipientJpaRepository
                .findByEmailIgnoreCase(recipient.getEmail())
                .orElse(recipient);
    }

    private Set<Book> reduceBooks(Set<OrderItem> items) {
           return items
                   .stream()
                   .map(item -> {
                        Book book = item.getBook();
                        book.setAvailable(book.getAvailable() - item.getQuantity());
                        return book;
                   })
                   .collect(Collectors.toSet());
    }

    private OrderItem toOrderItem(OrderItemCommand command) {
        Book book = getOneBook(command);
        int quantity = command.getQuantity();
        quantityCheck(book, quantity);
        return new OrderItem(book, quantity);
    }

    private Book getOneBook(OrderItemCommand command) {
        Optional<Book> bookOptional = bookJpaRepository.findById(command.getBookId());
        if(bookOptional.isEmpty()) {
            throw new IllegalArgumentException("The book with id: " + command.getBookId() + " does not exist in our repository.");
        }
        return bookOptional.get();
    }

    private void quantityCheck(Book book, int quantity) {
        if (quantity < 0) {
        throw new IllegalArgumentException("Quantity cannot be negative!");
        }
        if (book.getAvailable() < quantity) {
            throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available.");
        }
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UpdateStatusResponse updateOrderStatus(UpdateStatusCommand command) {
        return repository.findById(command.getOrderId())
                .map(order -> {
                    if(!hasAccess(command, order)) {
                        return UpdateStatusResponse.failure("Unauthorized");
                    }
                    UpdateStatusResult result = order.updateStatus(command.getStatus());
                    if(result.isRevoked()) {
                        bookJpaRepository.saveAll(revokeBooks(order.getItems()));
                    }
                    repository.save(order);
                    return UpdateStatusResponse.success(order.getStatus());
                })
                .orElse(UpdateStatusResponse.failure("Order not found"));
    }

    private boolean hasAccess(UpdateStatusCommand command, Order order) {
        String email = command.getEmail();
        return email.equalsIgnoreCase(order.getRecipient().getEmail()) ||
                email.equalsIgnoreCase("admin@example.org");
    }

    private Set<Book> revokeBooks(Set<OrderItem> items) {
        return items
                .stream()
                .map(item -> {
                    Book book = item.getBook();
                    book.setAvailable(book.getAvailable() + item.getQuantity());
                    return book;
                })
                .collect(Collectors.toSet());
    }

}
