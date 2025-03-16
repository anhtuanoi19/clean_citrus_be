package vn.tuanla.clean_citrus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PERMISSIONS")
public class Permissions {
    @Id
    @SequenceGenerator(name = "PERMISSION_SEQ", sequenceName = "PERMISSION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PERMISSION_SEQ")
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "permissions")
    @Fetch(value = FetchMode.SELECT)
    @JsonIgnore
    private Set<Users> users = new HashSet<>();


    public Permissions(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Permissions(String name) {
        this.name = name;
    }
}
