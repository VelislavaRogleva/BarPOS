package app.repositories;

import app.entities.BarTable;
import app.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o WHERE o.status = 'OPEN' AND o.barTable = :b")
    Order findOpenOrderByBarTable(@Param("b") BarTable barTable);
}
