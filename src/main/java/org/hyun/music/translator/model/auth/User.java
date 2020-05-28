package org.hyun.music.translator.model.auth;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.Locale;

@Entity
@Table(name="users")
public class User implements Serializable {

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

    @NotBlank
    @Convert(converter = UserTypeConverter.class)
    @Column(name = "usertype")
    private String userType;

    @Column(name = "refreshtoken")
    private String refreshToken;

    @NotNull
    @Column(name = "isdisabled")
    private boolean mDisabled;

    @Column(name = "lastlogindatetime")
    private Date mLastLoginDate;

    @Column(name = "created")
    private Date mCreated;

    public Date getCreated() {
        return defensiveCopy(mCreated);
    }

    public void setCreated(Date created) {
        mCreated = defensiveCopy(created);
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

    public Date getLastLoginDate() {
        return defensiveCopy(mLastLoginDate);
    }

    public void setLastLoginDate(Date mLastLoginDate) {
        this.mLastLoginDate = defensiveCopy(mLastLoginDate);
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
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

    // Setting defensive copy so this information doesn't get manipulated
    private Date defensiveCopy(final Date dateToCopy){
        if(dateToCopy == null)
            return null;
        return new Date(dateToCopy.getTime());
    }

}
