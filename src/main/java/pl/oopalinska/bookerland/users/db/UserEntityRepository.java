package pl.oopalinska.bookerland.users.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.oopalinska.bookerland.users.domain.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
