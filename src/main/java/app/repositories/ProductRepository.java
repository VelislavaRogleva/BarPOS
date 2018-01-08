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
}