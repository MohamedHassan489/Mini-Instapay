package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Models.BankAccount;
import java.util.List;
import java.util.Optional;

public interface BankAccountRepository {
    boolean save(BankAccount account);
    List<BankAccount> findByOwner(String owner);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    boolean updateBalance(String accountNumber, double balance);
    boolean delete(String accountNumber);
}

