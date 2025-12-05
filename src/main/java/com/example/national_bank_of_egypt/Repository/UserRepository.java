package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Models.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean save(User user);
    boolean update(User user);
    boolean existsByUserName(String userName);
    boolean existsByPhoneNumber(String phoneNumber);
}

