package app.repositories;

import app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByOrderByIsActiveDesc();

    User findByName(String name);

    @Query(value = "SELECT * FROM users AS u WHERE u.name LIKE %:text%", nativeQuery = true)
    List<User> findByNameMatchingText(@Param("text") String text);

    @Query(value = "SELECT * FROM users AS u\n" +
            "WHERE u.is_active = TRUE", nativeQuery = true)
    List<User> findAllActiveUsers();
}