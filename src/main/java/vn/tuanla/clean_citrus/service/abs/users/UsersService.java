package vn.tuanla.clean_citrus.service.abs.users;

import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.domain.entity.Users;

public interface UsersService {
    Users saveUser(Users users);
    Permissions savePermission(Permissions permissions);
    void addToUser(String username, String permission);
    Users findUserByUsername(String username);
}
