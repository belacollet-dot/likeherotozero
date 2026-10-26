package de.likeherotozero.repository;

import de.likeherotozero.entity.Role;
import de.likeherotozero.entity.ScientistUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScientistUserRepository extends JpaRepository<ScientistUser, Long> {

    Optional<ScientistUser> findByEmail(String email);

    List<ScientistUser> findByRoleOrderByNameAsc(Role role);
}
