package com.waleed.profile.profile_backend.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waleed.profile.profile_backend.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>
{
}
