package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Exceptions.RepositoryException;
import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import com.example.national_bank_of_egypt.Models.User;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final DataBaseDriver dataBaseDriver;

    public UserRepositoryImpl(DataBaseDriver dataBaseDriver) {
        this.dataBaseDriver = dataBaseDriver;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        try {
            ResultSet rs = dataBaseDriver.getUserData(userName, null);
            if (rs != null && rs.next()) {
                User user = new User(
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Address"),
                    rs.getString("UserName"),
                    rs.getString("Password"),
                    LocalDate.parse(rs.getString("DateCreated"))
                );
                user.twoFactorEnabledProperty().set(rs.getString("TwoFactorEnabled"));
                rs.close();
                return Optional.of(user);
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find user by username: " + userName, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        try {
            ResultSet rs = dataBaseDriver.getUserByPhoneNumber(phoneNumber);
            if (rs != null && rs.next()) {
                User user = new User(
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getString("Email"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Address"),
                    rs.getString("UserName"),
                    rs.getString("Password"),
                    LocalDate.parse(rs.getString("DateCreated"))
                );
                user.twoFactorEnabledProperty().set(rs.getString("TwoFactorEnabled"));
                rs.close();
                return Optional.of(user);
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find user by phone number: " + phoneNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(User user) {
        return dataBaseDriver.createUser(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getUserName(),
            user.getPassword(),
            user.dateCreatedProperty().get()
        );
    }

    @Override
    public boolean update(User user) {
        return dataBaseDriver.updateUser(
            user.getUserName(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getAddress()
        );
    }

    @Override
    public boolean existsByUserName(String userName) {
        return dataBaseDriver.userExists(userName);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return dataBaseDriver.phoneNumberExists(phoneNumber);
    }
}
