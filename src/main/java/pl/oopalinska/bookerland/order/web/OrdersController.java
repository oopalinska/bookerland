package pl.oopalinska.bookerland.order.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.oopalinska.bookerland.order.application.port.QueryOrderUseCase;
import pl.oopalinska.bookerland.order.application.RichOrder;
import pl.oopalinska.bookerland.order.domain.OrderStatus;
import pl.oopalinska.bookerland.security.UserSecurity;
import pl.oopalinska.bookerland.web.CreatedURI;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrdersController {
    private final ManipulateOrderUseCase manipulateOrderService;
    private final QueryOrderUseCase queryOrderService;
    private final UserSecurity userSecurity;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RichOrder> getAllOrders() {
        return queryOrderService.findAll();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<RichOrder> getOrderById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return queryOrderService.findById(id)
                .map(order -> authorize(order, user))
                .orElse(ResponseEntity.notFound().build());
    }
    private ResponseEntity<RichOrder> authorize(RichOrder order, User user) {
        if (userSecurity.isOwnerOrAdmin(order.getRecipient().getEmail(), user)) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.status(FORBIDDEN).build();
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

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
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
    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrderService.deleteOrderById(id);
    }
}