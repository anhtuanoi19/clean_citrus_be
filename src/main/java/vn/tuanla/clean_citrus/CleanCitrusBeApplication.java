package vn.tuanla.clean_citrus;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.tuanla.clean_citrus.domain.entity.Permissions;
import vn.tuanla.clean_citrus.domain.entity.Users;
import vn.tuanla.clean_citrus.service.abs.users.UsersService;

import java.util.HashSet;

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
public class CleanCitrusBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleanCitrusBeApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner init(UsersService usersService) {
        return args -> {
//            usersService.savePermission(new Permissions(null, "ADMIN"));
//            usersService.savePermission(new Permissions(null, "USER"));
//
//            usersService.saveUser(new Users(null, "admin","admin","admin@gamil.com", new HashSet<>()));
//            usersService.saveUser(new Users(null, "user","user","user@gamil.com", new HashSet<>()));
//
//            usersService.addToUser("admin", "ADMIN");
//            usersService.addToUser("admin", "USER");
//            usersService.addToUser("user", "USER");
        };
    }
}
