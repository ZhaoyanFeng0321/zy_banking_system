package zycode.web.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zycode.web.app.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);
}
