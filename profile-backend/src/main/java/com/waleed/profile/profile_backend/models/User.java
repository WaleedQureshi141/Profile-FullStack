package com.waleed.profile.profile_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User 
{
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "firstname")
    private String fn;

    @Column(name = "lastname")
    private String ln;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String pw;

    @Column(name = "role")
    private String role;
}
