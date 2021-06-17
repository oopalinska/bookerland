package pl.oopalinska.bookerland.order.application.port;

import lombok.*;
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
    class PlaceOrderResponse {
        boolean success;
        Long orderId;
        List<String> errors;

        public static PlaceOrderResponse success(Long orderId) {
            return new PlaceOrderResponse(true, orderId, emptyList());
        }
        public static PlaceOrderResponse failure(String... errors) {
            return new PlaceOrderResponse(false, null, Arrays.asList(errors));
        }
    }
}
