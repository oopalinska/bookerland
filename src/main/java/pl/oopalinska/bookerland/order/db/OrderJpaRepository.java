package pl.oopalinska.bookerland.order.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.oopalinska.bookerland.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

}
