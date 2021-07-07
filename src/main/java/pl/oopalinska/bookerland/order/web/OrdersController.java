package pl.oopalinska.bookerland.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.application.RichOrder;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.web.CreatedURI;

import java.net.URI;
import java.util.List;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrdersController {
    private final ManipulateOrderUseCase manipulateOrderService;
    private final QueryOrderUseCase queryOrderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> getAllOrders() {
        return queryOrderService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody PlaceOrderCommand command) {
        return manipulateOrderService
                .placeOrder(command)
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
}
    private URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        var orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + command.status));
        manipulateOrderService.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrderService.deleteOrderById(id);
    }

    @Data
    static class UpdateStatusCommand {
        String status;
    }
}