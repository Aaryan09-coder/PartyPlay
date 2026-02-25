package com.vibey.PartPlay.Security;

import com.vibey.PartPlay.Entity.Users;
import com.vibey.PartPlay.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    // Spring Security calls this automatically when POST /login is submitted.
    // The value passed here is whatever was typed in the "username" input field.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user found with username: " + username)
                );
        return new UserPrincipal(user);
    }
}
