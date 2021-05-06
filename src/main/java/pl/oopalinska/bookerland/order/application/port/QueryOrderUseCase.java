package pl.oopalinska.bookerland.order.application.port;

import lombok.Value;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {
    List<RichOrder> findAll();
    Optional<RichOrder> findById(Long id);

    @Value
    class RichOrder {
        Long id;
        OrderStatus status;
        List<RichOrderItem> items;
        Recipient recipient;
        LocalDateTime createdAt;

        public BigDecimal totalPrice() {
            return items.stream()
                    .map(item -> item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    @Value
    class RichOrderItem {
        Book book;
        int quantity;
    }
}
