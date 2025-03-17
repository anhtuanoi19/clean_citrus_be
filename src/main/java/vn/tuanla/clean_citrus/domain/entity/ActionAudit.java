package vn.tuanla.clean_citrus.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "ACTION_AUDIT")
public class ActionAudit extends BaseEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "ID", nullable = false)
   private Long id;

   @Column(name="ACTION_DATETIME")
   private Date actionDatetime;

   @Column(name="ACTION_CODE")
   private String actionCode;

   @Column(name="TABLE_NAME")
   private String tableName;

   @Column(name="DESCRIPTION")
   private String description;

   @Column(name="OBJECT_ID")
   private Long objectId;

   @Column(name="OBJECT_parent_id")
   private Long objectParentId;

   @Column(name="STATUS")
   private Integer status;

   @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "actionAudit", fetch = FetchType.LAZY)
   List<ActionDetail> actionDetails;

}
