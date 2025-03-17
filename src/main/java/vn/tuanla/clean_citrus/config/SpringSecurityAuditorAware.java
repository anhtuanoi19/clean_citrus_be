package vn.tuanla.clean_citrus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.tuanla.clean_citrus.domain.entity.Users;
import vn.tuanla.clean_citrus.exception.BusinessException;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityAuditorAware.class);

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.warn("No authentication in security context. Now return admin as user for auditing.");
            return Optional.empty();
        } else {
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                // Authentication principal was set in JwtAuthenticationFilter
                UsernamePasswordAuthenticationToken usernamePasswordToken =
                        ((UsernamePasswordAuthenticationToken) authentication);
                Users userMinDTO = (Users) usernamePasswordToken.getPrincipal();
                return Optional.of(userMinDTO.getUsername());
            } else {
                throw new BusinessException("No authentication in security context. Now return admin as user for auditing");
            }
        }
    }
}
