package pl.oopalinska.bookerland.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.application.port.PlaceOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.PlaceOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.PlaceOrderUseCase.PlaceOrderResponse;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderItem;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.order.domain.Recipient;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrderController {
    private final PlaceOrderUseCase placeOrderService;
    private final QueryOrderUseCase queryOrderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAllOrders() {
        return queryOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return queryOrderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        PlaceOrderResponse response = placeOrderService.placeOrder(command.toPlaceOrderCommand());
        if (!response.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.getErrors().toString());
        }
            return ResponseEntity.created(createdOrderUri(response.getOrderId())).build();
    }
    private URI createdOrderUri(Long orderId) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/" + orderId.toString()).build().toUri();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        var orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't update... " + command.status));
        placeOrderService.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        placeOrderService.deleteOrderById(id);
    }

    @Data
    static class CreateOrderCommand {
        List<OrderItemCommand> items;
        RecipientCommand recipient;

        PlaceOrderCommand toPlaceOrderCommand() {
            List<OrderItem> orderItems = items
                    .stream()
                    .map(item -> new OrderItem(item.getBook(), item.quantity))
                    .collect(Collectors.toList());
            return new PlaceOrderCommand(orderItems, recipient.toRecipient());
        }
    }
    @Data
    static class OrderItemCommand {
        Book book;
        int quantity;
    }

    @Data
    static class RecipientCommand {
        String name;
        String phone;
        String street;
        String city;
        String zipCode;
        String email;

        Recipient toRecipient() {
            return new Recipient(name, phone, street, city, zipCode, email);
        }
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}