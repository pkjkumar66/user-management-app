package com.example.app.entity;

import com.example.app.util.PasswordUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = "my_user")
@ToString(callSuper = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String passwordHash;

    @Column(name = "salt")
    private String salt;

    public User() {
    }

    public User(String userName, String password, PasswordUtils passwordUtils) {
        this.userName = userName;
        setPasswordHash(password, passwordUtils);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setPasswordHash(String password, PasswordUtils passwordUtils) {
        this.salt = passwordUtils.generateSalt();
        this.passwordHash = passwordUtils.hashPassword(password, this.salt);
    }
}
