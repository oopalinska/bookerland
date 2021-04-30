package pl.oopalinska.bookerland.order.application.port;

import pl.oopalinska.bookerland.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {
    List<Order> findAll();
}
