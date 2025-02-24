package com.waleed.profile.profile_backend.Services;

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.waleed.profile.profile_backend.DTOs.UserInfoDTO;
import com.waleed.profile.profile_backend.models.User;
import com.waleed.profile.profile_backend.repos.UserRepo;

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
    public List<UserInfoDTO> getAllUsers()
    {
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

    // GET: find all users => /{id}
    // ADMIN
    public UserInfoDTO getUserById(int id)
    {
        User user = userRepo.findById(id).get();
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
    // USER + ADMIN
    public String deleteUser(int id)
    {
        userRepo.deleteById(id);
        return "USER DELETED";
    }

    // PATCH: update user info => /{id}
    // USER
    public String updateUser(int id, UserInfoDTO user)
    {
        if (
            user.getFn().isBlank() 
            || user.getLn().isBlank()
        )
        {
            return null;
        }

        User updUser = userRepo.findById(id).get();
        updUser.setFn(user.getFn());
        updUser.setLn(user.getLn());

        userRepo.save(updUser);

        return "USER UPDATED";
    }

    // PATCH: promote to ADMIN => /{id}
    // ADMIN
    public String promoteUser(int id)
    {
        User user = userRepo.findById(id).get();
        user.setRole("ADMIN");

        userRepo.save(user);

        return "USER PROMOTED";
    }
}
