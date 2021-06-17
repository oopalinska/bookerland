package pl.oopalinska.bookerland.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.oopalinska.bookerland.catalog.db.BookJpaRepository;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.db.OrderJpaRepository;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.OrderStatus;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    private final OrderJpaRepository repository;
    private final BookJpaRepository bookJpaRepository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        final Set<OrderItem> items = command.getItems()
                                            .stream()
                                            .map(this::toOrderItem)
                                            .collect(Collectors.toSet());
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(items)
                .build();
        Order save = repository.save(order);
        bookJpaRepository.saveAll(updateBooks(items));
        return PlaceOrderResponse.success(save.getId());
    }

    private Set<Book> updateBooks(Set<OrderItem> items) {
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
        Book book = bookJpaRepository.getOne(command.getBookId());
        int quantity = command.getQuantity();
        if (book.getAvailable() >= quantity) {
            return new OrderItem(book, quantity);
        }
        throw new IllegalArgumentException("Too many copies of book " + book.getId() + " requested: " + quantity + " of " + book.getAvailable() + " available.");
    }

    @Override
    public void deleteOrderById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateOrderStatus(Long id, OrderStatus status) {
        repository.findById(id)
                .ifPresent(order -> {
                    order.updateStatus(status);
                    repository.save(order);
                });
    }
}
