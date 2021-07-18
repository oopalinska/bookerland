package pl.oopalinska.bookerland.order.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.application.RichOrder;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.web.CreatedURI;

import java.net.URI;
import java.util.List;
import java.util.Map;

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

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        var orderStatus = OrderStatus
                .parseString(status)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown status: " + status));
        UpdateStatusCommand command = new UpdateStatusCommand(id, orderStatus, "admin@example.org");
        return manipulateOrderService
                .updateOrderStatus(command)
                .handle(
                        givenStatus -> ResponseEntity.accepted().body(givenStatus),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrderService.deleteOrderById(id);
    }
}