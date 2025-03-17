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
public class ActionDetailDTO {
   private Long id;
   private String colName;
   private String colDisplayName;
   private String oldValue;
   private String newValue;
   private String tableName;
   private String oldDisplayValue;
   private String newDisplayValue;
   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date issueDate;
   private Long staffId;
   private String tableDisplayName;
   private Long rowId;
   private String parentColumnOldValue;
   private Long actionAuditId;
   private String practiceColumnNewValue;
   private String actionType;
   private Integer status;
   private String createdBy;
   private String lastModifiedBy;
   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date createdDate;
   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date lastModifiedDate;
   private Integer deleted;
   @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy",timezone = "Asia/Saigon" )
   private Date deletedDate;
   private String deletedBy;
}
