package com.waleed.profile.profile_backend.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waleed.profile.profile_backend.DTOs.UserInfoDTO;
import com.waleed.profile.profile_backend.Services.UserService;
import com.waleed.profile.profile_backend.models.User;

@RestController
@RequestMapping("/api")
public class UserController 
{
    private final UserService service;
    public UserController(UserService service)
    {
        this.service = service;
    }

    // GET: find all users => /
    // ADMIN
    @GetMapping("/")
    public ResponseEntity<List<UserInfoDTO>> allUsers(@RequestHeader("Authorization") String token)
    {
        if (service.getAllUsers(token) == null)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(service.getAllUsers(token), HttpStatus.OK);
    }

    // GET: find user by id => /{id}
    // USER
    @GetMapping("/user")
    public ResponseEntity<UserInfoDTO> user(@RequestHeader("Authorization") String token)
    {
        return new ResponseEntity<>(service.getUserById(token), HttpStatus.OK);
    }

    // POST: add user => /register
    // ALL
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user)
    {
        String res = service.addUser(user);
        if (res == null)
        {
            return new ResponseEntity<>("USER INFORMATION MISSING/INVALID", HttpStatusCode.valueOf(400));
        }

        if (res == "CONFLICT")
        {
            return new ResponseEntity<>("USERNAME ALREADY EXISTS", HttpStatus.CONFLICT);
        }
        
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // POST: login => /login
    // ALL
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user)
    {
        String res = service.loginUser(user);

        if (res == "BAD_REQUEST")
        {
            return new ResponseEntity<>("MISSING FIELDS", HttpStatus.BAD_REQUEST);
        }
        if (res == "FORBIDDEN")
        {
            return new ResponseEntity<>("INVALID CREDENTIALS", HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // DELETE: delete user => /{id}
    // ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delUser(@RequestHeader("Authorization") String token, @PathVariable int id)
    {
        return new ResponseEntity<>(service.deleteUser(token, id), HttpStatus.OK);
    }

    // PATCH: update user info => /{id}
    // USER
    @PatchMapping("/update")
    public ResponseEntity<String> updUser(@RequestHeader("Authorization") String token, @RequestBody UserInfoDTO user)
    {
        String res = service.updateUser(token, user);
        if (res == null)
        {
            return new ResponseEntity<>("USER INFORMATION MISSING", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // PATCH: promote to ADMIN => /{id}
    // ADMIN
    @PatchMapping("/promote/{id}")
    public ResponseEntity<String> prmtUser(@RequestHeader("Authorization") String token, @PathVariable int id)
    {
        if (service.promoteUser(token, id) == "FORBIDDEN")
        {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(service.promoteUser(token, id), HttpStatus.OK);
    }
}
