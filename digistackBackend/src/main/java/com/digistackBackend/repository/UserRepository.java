package com.digistackBackend.repository;

import com.digistackBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    //Handy method to find a user by their email.

    Optional<User> findByName(String name);
    //Handy method to find a user by their users name.

    Optional<User> findByMobileNumber(String mobileNumber);
    //Handy method to find a user by their mobile number.
}
