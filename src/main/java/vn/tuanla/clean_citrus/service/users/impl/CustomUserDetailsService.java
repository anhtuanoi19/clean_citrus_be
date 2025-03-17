package vn.tuanla.clean_citrus.service.users.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.tuanla.clean_citrus.domain.entity.Users;
import vn.tuanla.clean_citrus.repository.jpa.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository userRepository;

    public CustomUserDetailsService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy các permission của người dùng và chuyển thành authority
        List<GrantedAuthority> authorities = user.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))  // Quyền (permission)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
