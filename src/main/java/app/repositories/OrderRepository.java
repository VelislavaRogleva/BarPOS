package app.repositories;

import app.entities.BarTable;
import app.entities.Order;
import app.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT o FROM Order o WHERE o.status = 'OPEN' AND o.barTable = :b")
    Order findOpenOrderByBarTable(@Param("b") BarTable barTable);

    @Query(value = "SELECT o FROM Order o WHERE o.status = :status AND o.date BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("status") OrderStatus orderStatus, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Modifying
    @Query(value = "UPDATE Order o SET o.status= :s WHERE o.id = :id")
    void changeOrderStatus(@Param("s")OrderStatus orderStatus, @Param("id") Long id);
}