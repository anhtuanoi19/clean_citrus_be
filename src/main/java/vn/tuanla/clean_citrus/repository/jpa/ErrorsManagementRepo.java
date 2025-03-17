package vn.tuanla.clean_citrus.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tuanla.clean_citrus.domain.entity.SystemErrorsManagement;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErrorsManagementRepo extends JpaRepository<SystemErrorsManagement, Long> {
    List<SystemErrorsManagement> findAllByDeletedAndStatus(int deleted, int status);

    @Query("FROM SystemErrorsManagement WHERE errorCode = :errorCode AND status = :status AND deleted = :deleted")
    Optional<SystemErrorsManagement> findByErrorCodeAndLang(String errorCode, int deleted, int status);

}
