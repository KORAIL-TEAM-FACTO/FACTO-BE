package team.java.facto_be.global.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import team.java.facto_be.global.security.jwt.domain.entity.types.Role;

/**
 * Stub admin details service for ADMIN role authentication.
 */
@Component
public class CustomAdminDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String adminId){
        return new AuthDetails(adminId, Role.ADMIN.name(), AuthDetails.EMPTY_ATTRIBUTES);
    }
}
