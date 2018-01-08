package app.repositories;

import app.entities.Category;
import app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByAvailableDesc();

    List<Product> findAllByCategory(Category category);

    List<Product> findAllByAvailable(Boolean available);

    List<Product> findAllByAvailableAndCategory(Boolean available, Category category);

    Product findById(Long id);

    Product findByName(String name);

    @Query(value = "SELECT * FROM products as p WHERE p.name LIKE %:text%",
            nativeQuery = true)
    List<Product> findAllProductsMatching(@Param("text") String name);

    @Query(value = "SELECT p.name, p.cost, p.price, (p.price - p.cost) AS profit, SUM(op.product_quantity) AS sold\n" +
            "FROM orders AS o\n" +
            "INNER JOIN order_products AS op\n" +
            "    ON op.order_id = o.id\n" +
            "INNER JOIN products p\n" +
            "    ON op.product_id = p.id\n" +
            "WHERE o.date >= date(:startDate)\n" +
            "      AND o.date <= date(:endDate)\n" +
            "      AND o.status LIKE :status\n" +
            "GROUP BY p.id\n" +
            "ORDER BY sold DESC",
            nativeQuery = true)
    List<Object[]> findAllSoldProductsOrOrderByTotalSoldAmountDesc(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("status") String status);
}