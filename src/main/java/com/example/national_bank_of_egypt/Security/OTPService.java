package com.example.national_bank_of_egypt.Security;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OTPService {
    private static OTPService instance;
    private final ConcurrentHashMap<String, String> otpStore;
    private final ConcurrentHashMap<String, Long> otpExpiry;
    private final Random random;
    private final ScheduledExecutorService scheduler;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;

    private OTPService() {
        this.otpStore = new ConcurrentHashMap<>();
        this.otpExpiry = new ConcurrentHashMap<>();
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.emailService = EmailService.getInstance();
    }

    public static synchronized OTPService getInstance() {
        if (instance == null) {
            instance = new OTPService();
        }
        return instance;
    }

    /**
     * Generates OTP and stores it
     * @param userId User ID
     * @return Generated OTP
     */
    public String generateOTP(String userId) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStore.put(userId, otp);
        otpExpiry.put(userId, System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000));
        
        scheduler.schedule(() -> {
            otpStore.remove(userId);
            otpExpiry.remove(userId);
        }, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        return otp;
    }
    
    /**
     * Generates OTP and sends it via email
     * @param userId User ID
     * @param userEmail User's email address
     * @param userName User's display name
     * @return true if OTP generated and email sent successfully
     */
    public boolean generateAndSendOTP(String userId, String userEmail, String userName) {
        String otp = generateOTP(userId);
        return emailService.sendOTPEmail(userEmail, userName, otp);
    }

    public boolean verifyOTP(String userId, String otp) {
        String storedOTP = otpStore.get(userId);
        if (storedOTP == null) {
            return false;
        }
        
        Long expiryTime = otpExpiry.get(userId);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            otpStore.remove(userId);
            otpExpiry.remove(userId);
            return false;
        }
        
        boolean isValid = storedOTP.equals(otp);
        if (isValid) {
            otpStore.remove(userId);
            otpExpiry.remove(userId);
        }
        return isValid;
    }
}

