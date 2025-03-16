package vn.tuanla.clean_citrus.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.domain.entity.Users;
import vn.tuanla.clean_citrus.repository.jpa.PermissionRepo;
import vn.tuanla.clean_citrus.repository.jpa.UsersRepository;

@Service
public class UserServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PermissionRepo permissionsRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository, PermissionRepo permissionsRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.permissionsRepository = permissionsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Users saveUser(Users users) {
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        return usersRepository.save(users);
    }

    @Override
    public Permissions savePermission(Permissions permissions) {
        return permissionsRepository.save(permissions);
    }

    @Override
    @Transactional
    public void addToUser(String username, String permission) {
        Users users = usersRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));
        Permissions permissions = permissionsRepository.findByName(permission).orElseThrow(()->new RuntimeException("Permission not found"));
        users.getPermissions().add(permissions);
    }
}
