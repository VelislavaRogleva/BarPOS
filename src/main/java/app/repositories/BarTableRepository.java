package app.repositories;

import app.entities.BarTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarTableRepository extends JpaRepository<BarTable, Long> {
}
