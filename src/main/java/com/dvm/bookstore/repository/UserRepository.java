package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String username);
    Boolean existsUserByUserName(String username);
    Boolean existsUserByEmail(String email);
}
