package com.example.firebase_auth.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// UserDetails is a contract for a user

// By implementing UserDetails, your class (e.g., UserModel) promises to provide all 
// information Spring

// UserDetails provides all user info (username, password, roles, status).

// Spring Security uses it to authenticate: check password, check account status.

// Spring Security uses it to authorize: check roles/authorities for protected endpoints.
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
    private String phone;
    private String password;

    private String firebaseUid;
    private String currentSessionId;
    private LocalDateTime lastLogin;


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private Role role;


    private String roleName;


      public boolean isLoggedInElsewhere(String currentSessionId) {
        return this.currentSessionId != null && 
               !this.currentSessionId.equals(currentSessionId) &&
               this.lastLogin != null &&
               this.lastLogin.isAfter(LocalDateTime.now().minusHours(1));
    }


    // these methos tells Spring security about user


    // Returns the permissions/roles of the user

    // So the claims are not stored in UserModel directly — they are read from the
    //  UserModel when generating the token.
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {

       return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }


    // Login username → here it’s email.

    // this is sub
    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }



//     These are status flags for the account.

// All true means the account is active and usable.
// These four methods in UserDetails are status flags that tell Spring Security whether the 
// account is usable. Right now, you have them all returning true, which basically says: 
// “this account is always active and valid.”
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }



//     1️⃣ isAccountNonExpired()

// Checks if the account has expired.

// true → account is still valid.

// false → user cannot log in because their account expired.

// 2️⃣ isAccountNonLocked()

// Checks if the account is locked (maybe due to too many failed login attempts).

// true → account is not locked.

// false → account is locked, login denied.

// 3️⃣ isCredentialsNonExpired()

// Checks if the password (credentials) has expired.

// true → password is still valid.

// false → user must reset password before login.

// 4️⃣ isEnabled()

// Checks if the account is enabled or active.

// true → user is active and can log in.

// false → account is disabled, maybe by admin.
}
