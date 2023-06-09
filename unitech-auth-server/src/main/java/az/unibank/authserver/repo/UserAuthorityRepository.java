package az.unibank.authserver.repo;


import az.unibank.authserver.models.RoleAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAuthorityRepository extends JpaRepository<RoleAuthorities, Integer> {


    @Query(value = "select a.name from RoleAuthorities ra join ra.authority a join ra.role u where u.id=:roleId")
    List<String> findAuthoritiesByUser(@Param("roleId") int roleId);
}