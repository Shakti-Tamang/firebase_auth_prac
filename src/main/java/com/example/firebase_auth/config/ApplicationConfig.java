package com.example.firebase_auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.firebase_auth.repo.UserReposiitory;

@Configuration
public class ApplicationConfig {

//     “Loading a user” means:

// Fetch the user’s full record from your database using the username (here → email).

// Wrap that user in a UserDetails object (your UserModel implements UserDetails).

// Give that object to Spring Security so it can:

// check the password,

// read the roles/authorities,

// and confirm whether the user is active, locked, expired, etc.

// ⚙️ Flow (simple example):

// You call login API → send email + password

// Spring calls userDetailsService.loadUserByUsername(email)

// That method queries your database → finds a UserModel

// Then Spring uses the data (especially password + roles) to authenticate

    private final UserReposiitory userReposiitory;

    public ApplicationConfig(UserReposiitory userReposiitory) {
        this.userReposiitory = userReposiitory;
    }


//     So this method:

// Queries your database (via UserRepository)

// Returns a UserModel (which implements UserDetails)

// When someone tries to log in (email + password),
// Spring calls this method automatically.

// It passes the email (used as username) into this function →
// findByEmail(username)

// That method looks up your database table and finds the user with that email.

// The result (your UserModel) is returned to Spring.

// Your UserModel implements UserDetails,
// so Spring can directly use it for authentication.

// Then Spring checks:

// Password (matches with DB using BCrypt)

// Role / authorities

// Account status (enabled, locked, expired, etc.)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userReposiitory.findByEmail(username);
        // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // DaoAuthenticationProvider → calls UserDetailsService.loadUserByUsername()
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());

//         Spring compares passwords

// Once it gets your UserModel, it checks
// If the password matches → Authentication success
// If not → Throws BadCredentialsException:


        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }



//     Spring Boot automatically creates an internal AuthenticationManager
// (based on your security configuration — i.e., providers, encoders, etc.)

// But it is not directly available as a bean to use in your own classes.


// “Hey Spring, take the authentication manager that you already built internally
//  (via AuthenticationConfiguration) — and make it a bean, so I can @Autowired it anywhere.”


// and returns that same AuthenticationManager object.

// Then, because of @Bean, it’s registered in the Spring container and becomes available
//  for injection.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}