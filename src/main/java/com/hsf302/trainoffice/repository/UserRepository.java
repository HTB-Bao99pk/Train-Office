package com.hsf302.trainoffice.repository;

import com.hsf302.trainoffice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User findByEmailAndPassword(String email, String password);
}
