package pl.oopalinska.bookerland.order.application.price;

import pl.oopalinska.bookerland.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
