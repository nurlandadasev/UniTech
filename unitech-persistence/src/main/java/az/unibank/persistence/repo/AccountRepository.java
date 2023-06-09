package az.unibank.persistence.repo;

import az.unibank.persistence.domains.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("select a from Account a join fetch a.currency where current date <= a.endDate and a.isActive=true")
    List<Account> findAllActiveAccountsByUserId(long userId);

    @EntityGraph(attributePaths = "currency", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Account> findAccountByIdAndUserId(long accountId, long userId);


}
