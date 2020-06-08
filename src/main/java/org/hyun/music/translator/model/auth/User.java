package org.hyun.music.translator.model.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name="users")
public class User implements Serializable, UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private long userId;

    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    @Column(name = "firstname")
    private String firstName;

    @NotBlank
    @Column(name = "lastname")
    private String lastName;

    @NotNull
    @Convert(converter = UserTypeConverter.class)
    @Column(name = "usertype")
    private UserType userType;

    @Column(name = "refreshtoken")
    private String refreshToken;

    @NotNull
    @Column(name = "isdisabled")
    private boolean mDisabled;

    @Column(name = "lastlogindatetime")
    private LocalDateTime mLastLoginDate;

    @Column(name = "created")
    private Timestamp mCreated;

    public Timestamp getCreated() {
        return mCreated;
    }

    public void setCreated(Timestamp created) {
        mCreated = created;
    }

    public User() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase(Locale.ROOT);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public boolean getDisabled() {
        return mDisabled;
    }

    public void setDisabled(boolean mDisabled) {
        this.mDisabled = mDisabled;
    }

    public LocalDateTime getLastLoginDate() {
        return mLastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime mLastLoginDate) {
        this.mLastLoginDate = mLastLoginDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final User other = (User) obj;
        if (username == null)
            return other.username == null;
        else
            return username.equals(other.username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(getAuthorityFromUserType(this.userType)));
        return grantedAuthority;
    }

    private String getAuthorityFromUserType(UserType userType) {
        switch (userType){
            case SUPER_USER:
                return Authority.SUPER_USER;
            case USER:
                return Authority.USER;
            default:
                return Authority.NO_AUTHORITY;
        }
    }

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
        return !this.mDisabled;
    }

}
