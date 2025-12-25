package com.example.national_bank_of_egypt.Repository;

import com.example.national_bank_of_egypt.Exceptions.RepositoryException;
import com.example.national_bank_of_egypt.Models.BankAccount;
import com.example.national_bank_of_egypt.Models.DataBaseDriver;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankAccountRepositoryImpl implements BankAccountRepository {
    private final DataBaseDriver dataBaseDriver;

    public BankAccountRepositoryImpl(DataBaseDriver dataBaseDriver) {
        this.dataBaseDriver = dataBaseDriver;
    }

    @Override
    public boolean save(BankAccount account) {
        return dataBaseDriver.createBankAccount(
            account.getOwner(),
            account.getAccountNumber(),
            account.getBankName(),
            account.getBalance(),
            account.getAccountType()
        );
    }

    @Override
    public List<BankAccount> findByOwner(String owner) {
        List<BankAccount> accounts = new ArrayList<>();
        try {
            ResultSet rs = dataBaseDriver.getBankAccounts(owner);
            if (rs != null) {
                while (rs.next()) {
                    accounts.add(new BankAccount(
                        rs.getString("Owner"),
                        rs.getString("AccountNumber"),
                        rs.getString("BankName"),
                        rs.getDouble("Balance"),
                        rs.getString("AccountType")
                    ));
                }
                rs.close();
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find bank accounts for owner: " + owner, e);
        }
        return accounts;
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        try {
            ResultSet rs = dataBaseDriver.getBankAccountByNumber(accountNumber);
            if (rs != null && rs.next()) {
                BankAccount account = new BankAccount(
                    rs.getString("Owner"),
                    rs.getString("AccountNumber"),
                    rs.getString("BankName"),
                    rs.getDouble("Balance"),
                    rs.getString("AccountType")
                );
                rs.close();
                return Optional.of(account);
            }
        } catch (Exception e) {
            throw new RepositoryException("Failed to find bank account by number: " + accountNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateBalance(String accountNumber, double balance) {
        return dataBaseDriver.updateBankAccountBalance(accountNumber, balance);
    }

    @Override
    public boolean delete(String accountNumber) {
        return dataBaseDriver.deleteBankAccount(accountNumber);
    }
}
