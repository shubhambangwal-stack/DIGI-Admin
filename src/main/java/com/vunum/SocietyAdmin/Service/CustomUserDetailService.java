package com.vunum.SocietyAdmin.Service;

import com.vunum.SocietyAdmin.entity.Admin;
import com.vunum.SocietyAdmin.entity.Users;
import com.vunum.SocietyAdmin.repository.AdminRepository;
import com.vunum.SocietyAdmin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if ("Pearl".equals(email)) {
            // In-memory admin user
            return User.withUsername("Pearl")
                    .password(passwordEncoder.encode("PearlProdChecker@12390"))
                    .authorities("SUPER_ADMIN")
                    .build();
        }

        Admin user = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email"));

        String password = user.getPassword();

        if (password == null || password.isEmpty()) {
            password = "";
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        if(user.getRole()!=null)
            authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        return new User(
                user.getEmail(),
                password,
                authorities);
    }


}
