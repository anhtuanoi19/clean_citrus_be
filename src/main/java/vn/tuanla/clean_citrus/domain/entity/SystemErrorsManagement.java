package vn.tuanla.clean_citrus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "system_errors_management")
public class SystemErrorsManagement extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "error_code", nullable = false, length = 100)
    private String errorCode;

    @Size(max = 250)
    @Nationalized
    @Column(name = "error_message_vi", length = 250)
    private String errorMessageVi;

    @Size(max = 250)
    @Nationalized
    @Column(name = "error_message_en", length = 250)
    private String errorMessageEn;

    @Size(max = 500)
    @Nationalized
    @Column(name = "descriptions", length = 500)
    private String descriptions;

    @Column(name = "status")
    private Integer status;

}