package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    User findByUsernameAndPasswordHash(String uname, String pwd);
}
