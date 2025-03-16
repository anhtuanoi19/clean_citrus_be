package vn.tuanla.clean_citrus.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import vn.tuanla.clean_citrus.domain.dto.request.AuthenticationRequest;
import vn.tuanla.clean_citrus.domain.dto.response.AuthenticationResponse;
import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.domain.entity.Users;
import vn.tuanla.clean_citrus.repository.dao.permission.impl.PermissionCustomRepoImpl;
import vn.tuanla.clean_citrus.repository.jpa.PermissionRepo;
import vn.tuanla.clean_citrus.repository.jpa.UsersRepository;
import vn.tuanla.clean_citrus.security.JwtTokenProvider;

import java.util.*;

@Service
public class AuthenticationService {

    private final UsersRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final PermissionCustomRepoImpl permissionRepo;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(UsersRepository usersRepository, AuthenticationManager authenticationManager, PermissionCustomRepoImpl permissionRepo, JwtTokenProvider jwtTokenProvider) {
        this.usersRepository = usersRepository;
        this.authenticationManager = authenticationManager;
        this.permissionRepo = permissionRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        Users users = usersRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        List<String> permissions = permissionRepo.getPermission(users); // Trả về List<String>

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (permissions != null) {
            permissions.forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission)); // Dùng trực tiếp chuỗi
            });
        }

        var jwtToken = jwtTokenProvider.generateToken(users, authorities);
        var jwtRefreshToken = jwtTokenProvider.generateRefreshToken(users, authorities);

        return AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken).build();
    }
}
