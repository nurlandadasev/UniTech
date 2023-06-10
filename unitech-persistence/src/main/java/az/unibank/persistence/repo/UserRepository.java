package az.unibank.persistence.repo;

import az.unibank.persistence.domains.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    @EntityGraph(attributePaths = "role", type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findUserById(long userId);

    User findByPin(String pin);

}
