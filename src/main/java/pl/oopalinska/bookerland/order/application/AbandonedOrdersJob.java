package pl.oopalinska.bookerland.order.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase;
import pl.oopalinska.bookerland.order.application.port.ManipulateOrderUseCase.UpdateStatusCommand;
import pl.oopalinska.bookerland.order.db.OrderJpaRepository;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AbandonedOrdersJob {
    private final OrderJpaRepository repository;
    private final ManipulateOrderUseCase orderUseCase;
    private final OrdersProperties properties;

    @Transactional
    @Scheduled(cron = "${app.orders.abandon-cron}")
    public void run() {
        Duration paymentPeriod = properties.getPaymentPeriod();
        LocalDateTime olderThan = LocalDateTime.now().minus(paymentPeriod);
        List<Order> orders = repository.findByStatusAndCreatedAtLessThanEqual(OrderStatus.NEW, olderThan);
        log.info("Found orders to be abandoned: " + orders.size());
        orders.forEach(order -> {
            String adminEmail = "admin@example.org";
            UpdateStatusCommand command = new UpdateStatusCommand(order.getId(), OrderStatus.ABANDONED, adminEmail);
            orderUseCase.updateOrderStatus(command);
        });
    }
}
