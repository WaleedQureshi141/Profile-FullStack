package com.waleed.profile.profile_backend.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO 
{
    int id;
    String fn;
    String ln;
    String username;
    String role;
}