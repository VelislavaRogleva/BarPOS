package app.repositories;

import app.entities.BarTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarTableRepository extends JpaRepository<BarTable, Long> {

    @Modifying
    @Query(value = "UPDATE BarTable b SET b.available = :s WHERE b.id = :id")
    void changeTableStatus(@Param("s") boolean status, @Param("id") Long id);

    @Query(value = "SELECT * FROM bar_table bt WHERE bt.available = 0 AND bt.id NOT IN " +
            "(SELECT bt.id FROM bar_table bt JOIN orders o ON bt.id = o.table_id WHERE o.status LIKE 'OPEN')" , nativeQuery = true)
    List<BarTable> getAllNotAvailableTablesWithoutOrders();
}