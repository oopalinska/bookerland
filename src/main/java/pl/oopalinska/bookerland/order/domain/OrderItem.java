package pl.oopalinska.bookerland.order.domain;

import lombok.Value;
import pl.oopalinska.bookerland.catalog.domain.Book;

@Value
public class OrderItem {
    Long bookId;
    int quantity;
}
