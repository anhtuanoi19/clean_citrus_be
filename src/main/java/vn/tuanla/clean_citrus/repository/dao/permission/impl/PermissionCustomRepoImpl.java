package vn.tuanla.clean_citrus.repository.dao.permission.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;
import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.domain.entity.Users;

import java.util.List;
@Repository
public class PermissionCustomRepoImpl{
    @PersistenceContext
    private EntityManager entityManager;

    public List<String> getPermission(Users users) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.name FROM users u " +
                "JOIN users_permission up ON u.id = up.users_id " +
                "JOIN permissions p ON up.permission_id = p.id ");
        sql.append("WHERE 1 = 1 ");
        if (users.getUsername() != null) {
            sql.append("AND u.username = :username ");
        }

        NativeQuery<String> query = ((Session) entityManager.getDelegate())
                .createNativeQuery(sql.toString(), String.class);

        if (users.getUsername() != null) {
            query.setParameter("username", users.getUsername());
        }

        return query.getResultList();
    }
}
