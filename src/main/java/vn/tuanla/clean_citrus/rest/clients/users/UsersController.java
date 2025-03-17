package vn.tuanla.clean_citrus.rest.clients.users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.tuanla.clean_citrus.common.Constants;
import vn.tuanla.clean_citrus.common.utils.ResponseFactory;
import vn.tuanla.clean_citrus.security.PermissionService;
import vn.tuanla.clean_citrus.service.abs.users.UsersService;

@RestController
@RequestMapping(Constants.CLIENT_API + "/users")
public class UsersController {
    private final ResponseFactory responseFactory;
    private final UsersService usersService;
    private final PermissionService permissionService;


    public UsersController(ResponseFactory responseFactory, UsersService usersService, PermissionService permissionService) {
        this.responseFactory = responseFactory;
        this.usersService = usersService;
        this.permissionService = permissionService;
    }


    @PreAuthorize("hasAuthority(@permissionService.getPermissionForUser('ADMIN'))")
    @GetMapping("/findByName")
    public ResponseEntity<?> findByName(@RequestParam String name) {
        return responseFactory.success(usersService.findUserByUsername(name));
    }
}
