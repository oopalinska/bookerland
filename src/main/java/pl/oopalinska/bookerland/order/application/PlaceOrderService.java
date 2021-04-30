package pl.oopalinska.bookerland.order.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.oopalinska.bookerland.order.application.port.PlaceOrderUseCase;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderRepository;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository repository;

    @Override
    public PlaceOrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = Order
                .builder()
                .recipient(command.getRecipient())
                .items(command.getItems())
                .build();
        Order save = repository.save(order);
        return PlaceOrderResponse.success(save.getId());
    }
}
