package com.example.national_bank_of_egypt.Utils;

import com.example.national_bank_of_egypt.Models.DataBaseDriver;

public class Disable2FA {
    public static void main(String[] args) {
        String userName = "middoo";
        
        DataBaseDriver db = new DataBaseDriver();
        boolean success = db.updateTwoFactorEnabled(userName, false);
        
        if (success) {
            System.out.println("Successfully disabled 2FA for user: " + userName);
        } else {
            System.out.println("Failed to disable 2FA for user: " + userName);
            System.out.println("User may not exist or there was a database error.");
        }
    }
}
