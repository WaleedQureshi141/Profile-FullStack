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
    public ResponseEntity<List<UserInfoDTO>> allUsers()
    {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }

    // GET: find all users => /{id}
    // ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> user(@PathVariable int id)
    {
        return new ResponseEntity<>(service.getUserById(id), HttpStatus.OK);
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
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // DELETE: delete user => /{id}
    // USER + ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delUser(@PathVariable int id)
    {
        return new ResponseEntity<>(service.deleteUser(id), HttpStatus.OK);
    }

    // PATCH: update user info => /{id}
    // USER
    @PatchMapping("/{id}")
    public ResponseEntity<String> updUser(@PathVariable int id, @RequestBody UserInfoDTO user)
    {
        String res = service.updateUser(id, user);
        if (res == null)
        {
            return new ResponseEntity<>("USER INFORMATION MISSING", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // PATCH: promote to ADMIN => /{id}
    // ADMIN
    @PatchMapping("/{id}")
    public ResponseEntity<String> prmtUser(@PathVariable int id)
    {
        return new ResponseEntity<>(service.promoteUser(id), HttpStatus.OK);
    }
}
