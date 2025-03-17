package vn.tuanla.clean_citrus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tuanla.clean_citrus.security.PermissionService;

@RestController
@RequestMapping("/demo")
public class TestController {
    private final PermissionService permissionService;

    public TestController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Authentication and authorization are working fine success");
    }


    @PreAuthorize("hasAuthority(@permissionService.getPermissionForUser('ADMIN'))")
    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Demo API - Accessible by dynamic USER role permission");
    }
}
