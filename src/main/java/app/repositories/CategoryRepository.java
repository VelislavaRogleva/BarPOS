package app.repositories;

import app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category getByName(String name);

    @Query(value = "SELECT * FROM categories AS c WHERE c.name LIKE %:text%",
    nativeQuery = true)
    List<Category> findCategoriesMatchingText(@Param("text") String text);
}