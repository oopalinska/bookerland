package pl.oopalinska.bookerland.order.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
    private final OrderRepository repository;

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
}
