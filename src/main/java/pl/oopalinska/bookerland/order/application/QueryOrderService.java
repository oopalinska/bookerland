package pl.oopalinska.bookerland.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.catalog.domain.CatalogRepository;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
    private final OrderRepository repository;
    private final CatalogRepository catalogRepository;

    public List<Order> findAll() {
        return new ArrayList<>(repository.findAll());
    }

    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id);
    }

//    private RichOrder toRichOrder(Order order) {
//        List<RichOrderItem> richItems = toRichItems(order.getItems());
//        return new RichOrder(
//                order.getId(),
//                order.getStatus(),
//                richItems,
//                order.getRecipient(),
//                order.getCreatedAt()
//        );
//    }
//
//    private List<RichOrderItem> toRichItems(List<OrderItem> items) {
//        return items.stream()
//                .map(item -> {
//                    Book book = catalogRepository
//                            .findById(item.getBookId())
//                            .orElseThrow(() -> new IllegalStateException("Unable to find book with ID: " + item.getBookId()));
//                    return new RichOrderItem(book, item.getQuantity());
//                })
//                .collect(Collectors.toList());
//    }
}
