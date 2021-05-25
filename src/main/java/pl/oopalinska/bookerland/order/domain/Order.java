package pl.oopalinska.bookerland.order.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private OrderStatus status = OrderStatus.NEW;

    @OneToMany
    @JoinColumn(name = "order id")
    private List<OrderItem> items;

    private transient Recipient recipient;

    private LocalDateTime createdAt;

}
