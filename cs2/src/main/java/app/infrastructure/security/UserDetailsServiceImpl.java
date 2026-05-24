package app.infrastructure.security;

import app.domain.ports.UserSystemPort;
import app.domain.models.UserSystem;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserSystemPort userSystemPort;

    public UserDetailsServiceImpl(UserSystemPort userSystemPort) {
        this.userSystemPort = userSystemPort;
    }

@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserSystem user = userSystemPort.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    
    return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getSystemRole().name()))
    );
}
}