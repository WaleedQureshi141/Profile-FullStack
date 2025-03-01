package com.waleed.profile.profile_backend.Services;

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.waleed.profile.profile_backend.DTOs.UserInfoDTO;
import com.waleed.profile.profile_backend.models.User;
import com.waleed.profile.profile_backend.repos.UserRepo;

import ch.qos.logback.core.subst.Token;

@Service
public class UserService 
{
    // field injection
    // easier but discouraged aproach to DI
    // makes testing harder
    // @Autowired
    // UserRepo userRepo;

    // setter injection
    // allows optional dependencies that can be changed at runtime
    // private UserRepo userRepo;
    // @Autowired
    // public void setUserRepo(UserRepo userRepo)
    // {
    //     this.userRepo = userRepo;
    // }

    // constructor injection
    // supports immutability and testing
    // recommended DI
    // allows to inject specific dependencies
    private final UserRepo userRepo;
    private final JwtService jwtService;
    public UserService(UserRepo userRepo, JwtService jwtService)
    {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    // GET: find all users => /
    // ADMIN
    public List<UserInfoDTO> getAllUsers(String token)
    {
        if (token == null || jwtService.decodeToken(token).getRole() != "ADMIN")
        {
            return null;
        }

        List<User> users = userRepo.findAll();
        List<UserInfoDTO> dto = new ArrayList<>();

        for (User user : users)
        {
            UserInfoDTO userDTO = new UserInfoDTO(
                user.getUserId(), user.getFn(), user.getLn(), user.getUsername(), user.getRole());
            dto.add(userDTO);
        }

        return dto;
    }

    // GET: find user by id => /{id}
    // USER
    public UserInfoDTO getUserById(String token)
    {
        User user = userRepo.findById(jwtService.decodeToken(token).getUserId()).get();
        UserInfoDTO dto = new UserInfoDTO(
            user.getUserId(), user.getFn(), user.getLn(), user.getUsername(), user.getRole());

        return dto;
    }

    // POST: add user => /register
    // ALL
    public String addUser(User user)
    {
        if (
            user.getFn().isBlank() 
            || user.getLn().isBlank()
            || user.getUsername().isBlank()
            || user.getPw().isBlank()
            || !user.getPw().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
        )
        {
            return null;
        }
        
        if (userRepo.findByUsername(user.getUsername()).isPresent())
        {
            return "CONFLICT";
        }

        user.setPw(BCrypt.hashpw(user.getPw(), BCrypt.gensalt(12)));
        user.setRole("USER");

        userRepo.save(user);
        return "USER REGISTERED";
    }

    // POST: login => /login
    // ALL
    public String loginUser(User user)
    {
        if (user.getUsername().isBlank() || user.getPw().isBlank())
        {
            return "BAD_REQUEST";
        }

        if (userRepo.findByUsername(user.getUsername()).isPresent())
        {
            User saved = userRepo.findByUsername(user.getUsername()).get();
            if (BCrypt.checkpw(user.getPw(), saved.getPw()))
            {
                return jwtService.generateToken(saved);
            }
            return "FORBIDDEN";
        }

        return "FORBIDDEN";
    }

    // DELETE: delete user => /{id}
    // ADMIN
    public String deleteUser(String token, int id)
    {
        if (jwtService.decodeToken(token).getRole() == "ADMIN")
        {
            userRepo.deleteById(id);
            return "USER DELETED";
        }
        
        return "FORBIDDEN";
    }

    // PATCH: update user info => /{id}
    // SPECIFIC USER
    public String updateUser(String token, UserInfoDTO user)
    {
        if (
            user.getFn().isBlank() 
            || user.getLn().isBlank()
        )
        {
            return "BAD_REQUEST";
        }

        if (userRepo.findById(jwtService.decodeToken(token).getUserId()).isPresent())
        {
            User updUser = userRepo.findById(jwtService.decodeToken(token).getUserId()).get();
            updUser.setFn(user.getFn());
            updUser.setLn(user.getLn());

            userRepo.save(updUser);

            return "USER UPDATED";
        }
        
        return "FORBIDDEN";
    }

    // PATCH: promote to ADMIN => /{id}
    // ADMIN
    public String promoteUser(String token, int id)
    {
        if (jwtService.decodeToken(token).getRole() == "ADMIN")
        {
            User user = userRepo.findById(id).get();
            user.setRole("ADMIN");
            userRepo.save(user);

            return "USER PROMOTED";
        }

        return "FORBIDDEN";
    }
}
