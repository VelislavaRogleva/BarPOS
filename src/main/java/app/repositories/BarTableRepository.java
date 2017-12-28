package app.repositories;

import app.entities.BarTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BarTableRepository extends JpaRepository<BarTable, Long> {

    @Modifying
    @Query(value = "UPDATE BarTable b SET b.available = :s WHERE b.id = :id")
    void changeTableStatus(@Param("s") boolean status, @Param("id") Long id);
}
