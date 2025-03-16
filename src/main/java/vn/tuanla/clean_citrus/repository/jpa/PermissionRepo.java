package vn.tuanla.clean_citrus.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.tuanla.clean_citrus.domain.entity.Permissions;

import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permissions,Long> {
    Optional<Permissions> findByName(String name);
}
