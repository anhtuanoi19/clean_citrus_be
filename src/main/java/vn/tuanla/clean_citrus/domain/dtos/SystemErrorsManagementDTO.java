package vn.tuanla.clean_citrus.domain.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemErrorsManagementDTO {

   private Long id;

   private String errorCode;

   private String errorMessageVi;

   private String errorMessageEn;

   private String descriptions;
   private Integer status;

   private Integer deleted;

   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date createdDate;

   private String createdBy;

   private String lastModifiedBy;

   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date lastModifiedDate;

   private String deletedBy;

   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date deletedDate;

   private String stt;
}
