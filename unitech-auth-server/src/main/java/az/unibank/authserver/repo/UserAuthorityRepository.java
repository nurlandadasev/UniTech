package az.unibank.authserver.repo;


import az.unibank.authserver.models.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Integer> {

//    @Query(value = "select a.name from user_authorities ua join authorities a on ua.authority_id = a.id where ua.user_id=:userId " +
//            "union " +
//            "select a.name from role_authorities ra join authorities a on ra.authority_id = a.id " +
//            "              join users u on u.role_id=ra.role_id " +
//            "              where u.id=:userId and ra.role_id=u.role_id and a.type=:authorityType " +
//            "order by name", nativeQuery = true)
    @Query(value = "select a.name from UserAuthority ua join ua.authority a join ua.role u where u.id=:roleId")
    List<String> findAuthoritiesByUser(@Param("roleId") int roleId);
}