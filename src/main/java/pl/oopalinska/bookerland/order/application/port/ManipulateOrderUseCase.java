package pl.oopalinska.bookerland.order.application.port;

import lombok.*;
import pl.oopalinska.bookerland.commons.Either;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.order.domain.Recipient;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public interface ManipulateOrderUseCase {
    PlaceOrderResponse placeOrder(PlaceOrderCommand command);

    @Builder
    @Value
    @AllArgsConstructor
    class PlaceOrderCommand {
        @Singular
        List<OrderItemCommand> items;
        Recipient recipient;
    }
    @Value
    class OrderItemCommand {
        Long bookId;
        int quantity;
    }

    void deleteOrderById(Long id);

    void updateOrderStatus(Long id, OrderStatus status);

    @Value
    class PlaceOrderResponse extends Either<String, Long> {
        public PlaceOrderResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }
        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, null, orderId);
        }
        public static PlaceOrderResponse failure(String error) {
            return new PlaceOrderResponse(false, error, null);
        }
    }
}
