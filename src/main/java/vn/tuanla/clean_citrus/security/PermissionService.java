package vn.tuanla.clean_citrus.security;

import org.springframework.stereotype.Service;
import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.repository.jpa.PermissionRepo;

import java.security.Permission;

@Service
public class PermissionService {
    private final PermissionRepo permissionRepository;

    public PermissionService(PermissionRepo permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
    public String getPermissionForUser(String role) {
        Permissions permission = permissionRepository.findByName(role)
                .orElseThrow(() -> new RuntimeException("Permission not found for role "));
        return permission.getName();
    }
}
