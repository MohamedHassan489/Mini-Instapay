package com.example.national_bank_of_egypt.Security;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * One-Time Password (OTP) Service for Two-Factor Authentication.
 * 
 * <p>This service generates, stores, and verifies One-Time Passwords for
 * additional security during sensitive operations like transactions or login.</p>
 * 
 * <h2>Design Pattern: Singleton Pattern</h2>
 * <p>Ensures only one instance exists application-wide, maintaining a single
 * OTP store for consistent verification across the application.</p>
 * 
 * <h2>Security Features:</h2>
 * <ul>
 *   <li><b>OTP Length:</b> 6 digits</li>
 *   <li><b>Expiry Time:</b> 5 minutes</li>
 *   <li><b>Single Use:</b> OTP is invalidated after successful verification</li>
 *   <li><b>Auto-Expiry:</b> OTPs are automatically removed after expiry</li>
 *   <li><b>Thread Safety:</b> Uses ConcurrentHashMap for thread-safe storage</li>
 * </ul>
 * 
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * OTPService service = OTPService.getInstance();
 * 
 * // Generate and send OTP
 * service.generateAndSendOTP("userId", "user@email.com", "John Doe");
 * 
 * // Later, verify the OTP entered by user
 * boolean isValid = service.verifyOTP("userId", "123456");
 * }</pre>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see EmailService
 */
public class OTPService {
    
    /** Singleton instance of the service */
    private static OTPService instance;
    
    /** Thread-safe storage for OTPs: userId -> OTP */
    private final ConcurrentHashMap<String, String> otpStore;
    
    /** Thread-safe storage for expiry timestamps: userId -> expiryTimeMillis */
    private final ConcurrentHashMap<String, Long> otpExpiry;
    
    /** Random number generator for OTP creation */
    private final Random random;
    
    /** Scheduler for automatic OTP cleanup after expiry */
    private final ScheduledExecutorService scheduler;
    
    /** Email service for sending OTPs to users */
    private final EmailService emailService;
    
    /** Number of digits in generated OTPs */
    private static final int OTP_LENGTH = 6;
    
    /** Time in minutes before an OTP expires */
    private static final long OTP_EXPIRY_MINUTES = 5;

    /**
     * Private constructor to enforce Singleton pattern.
     * 
     * <p>Initializes:
     * <ul>
     *   <li>Thread-safe OTP storage maps</li>
     *   <li>Random number generator</li>
     *   <li>Scheduled executor for expiry cleanup</li>
     *   <li>Email service for OTP delivery</li>
     * </ul>
     * </p>
     */
    private OTPService() {
        this.otpStore = new ConcurrentHashMap<>();
        this.otpExpiry = new ConcurrentHashMap<>();
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.emailService = EmailService.getInstance();
    }

    /**
     * Returns the singleton instance of OTPService.
     * 
     * <p>Thread-safe implementation using synchronized keyword.</p>
     * 
     * @return The singleton instance of OTPService
     */
    public static synchronized OTPService getInstance() {
        if (instance == null) {
            instance = new OTPService();
        }
        return instance;
    }

    /**
     * Generates a new OTP for the specified user.
     * 
     * <p>This method:
     * <ol>
     *   <li>Generates a random 6-digit OTP</li>
     *   <li>Stores it with the user ID</li>
     *   <li>Sets an expiry timestamp (5 minutes from now)</li>
     *   <li>Schedules automatic cleanup after expiry</li>
     * </ol>
     * </p>
     * 
     * <p><b>Note:</b> Generating a new OTP overwrites any existing OTP for the user.</p>
     * 
     * @param userId The unique identifier of the user
     * @return The generated 6-digit OTP string
     */
    public String generateOTP(String userId) {
        // Generate random 6-digit OTP (zero-padded)
        String otp = String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));
        
        // Store OTP and calculate expiry timestamp
        otpStore.put(userId, otp);
        otpExpiry.put(userId, System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000));
        
        // Schedule automatic cleanup after expiry time
        scheduler.schedule(() -> {
            otpStore.remove(userId);
            otpExpiry.remove(userId);
        }, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        return otp;
    }
    
    /**
     * Generates an OTP and sends it to the user via email.
     * 
     * <p>Combines OTP generation with email delivery for a complete
     * two-factor authentication flow.</p>
     * 
     * @param userId The unique identifier of the user
     * @param userEmail The user's email address for OTP delivery
     * @param userName The user's display name (used in email greeting)
     * @return {@code true} if OTP was generated and email sent successfully,
     *         {@code false} if email sending failed
     */
    public boolean generateAndSendOTP(String userId, String userEmail, String userName) {
        String otp = generateOTP(userId);
        return emailService.sendOTPEmail(userEmail, userName, otp);
    }

    /**
     * Verifies an OTP entered by the user.
     * 
     * <p>Verification process:
     * <ol>
     *   <li>Check if OTP exists for the user</li>
     *   <li>Check if OTP has not expired</li>
     *   <li>Compare provided OTP with stored OTP</li>
     *   <li>If valid, invalidate the OTP (single use)</li>
     * </ol>
     * </p>
     * 
     * @param userId The unique identifier of the user
     * @param otp The OTP entered by the user
     * @return {@code true} if OTP is valid and not expired, {@code false} otherwise
     */
    public boolean verifyOTP(String userId, String otp) {
        // Retrieve stored OTP for the user
        String storedOTP = otpStore.get(userId);
        
        // Return false if no OTP exists for this user
        if (storedOTP == null) {
            return false;
        }
        
        // Check if OTP has expired
        Long expiryTime = otpExpiry.get(userId);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            // OTP has expired - clean up and return false
            otpStore.remove(userId);
            otpExpiry.remove(userId);
            return false;
        }
        
        // Compare provided OTP with stored OTP
        boolean isValid = storedOTP.equals(otp);
        
        // If valid, invalidate the OTP (single use)
        if (isValid) {
            otpStore.remove(userId);
            otpExpiry.remove(userId);
        }
        
        return isValid;
    }
}

