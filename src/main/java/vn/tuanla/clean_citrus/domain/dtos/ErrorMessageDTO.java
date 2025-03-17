package vn.tuanla.clean_citrus.domain.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessageDTO implements Serializable {

        private Long id;

        private String errorCode;

        private String errorMessageVi;

        private String errorMessageEn;

        private String descriptions;

        private Integer status;

        private Integer deleted;

        private String deletedBy;

        private LocalDateTime deletedDate;
    }
