package pl.oopalinska.bookerland.order.infrastructure;

import pl.oopalinska.bookerland.catalog.domain.Book;
import pl.oopalinska.bookerland.order.domain.Order;
import pl.oopalinska.bookerland.order.domain.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong NEXT_ID = new AtomicLong(0L);

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Order save(Order order) {
        if (order.getId() != null) {
            storage.put(order.getId(), order);
        } else {
            long nextId = nextId();
            order.setId(nextId);
            order.setCreatedAt(LocalDateTime.now());
            storage.put(nextId, order);
        }
        return order;
    }

        private long nextId () {
            return NEXT_ID.getAndIncrement();
        }
    }

