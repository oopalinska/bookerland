package pl.oopalinska.bookerland.order.application.price;

import pl.oopalinska.bookerland.order.domain.Order;

import java.math.BigDecimal;

public class TotalPriceDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculate(Order order) {
        return null;
    }
}
