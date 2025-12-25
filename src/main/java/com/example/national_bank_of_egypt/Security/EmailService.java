package com.example.national_bank_of_egypt.Security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email Service for sending notifications via EmailJS API.
 * 
 * <p>This service handles email delivery for the application, primarily used
 * for sending One-Time Passwords (OTPs) during two-factor authentication.</p>
 * 
 * <h2>Design Pattern: Singleton Pattern</h2>
 * <p>Ensures only one instance exists application-wide, maintaining a single
 * HTTP client and consistent configuration.</p>
 * 
 * <h2>EmailJS Integration:</h2>
 * <p>This service uses the EmailJS REST API for email delivery. EmailJS is a
 * service that allows sending emails directly from client-side code without
 * requiring a backend server for email handling.</p>
 * 
 * <h2>Configuration Properties:</h2>
 * <ul>
 *   <li><b>serviceId:</b> Identifies the email service (e.g., Gmail, SMTP)</li>
 *   <li><b>templateId:</b> Email template with placeholders for dynamic content</li>
 *   <li><b>publicKey:</b> Client-side API key for EmailJS</li>
 *   <li><b>privateKey:</b> Server-side authentication key (for non-browser apps)</li>
 * </ul>
 * 
 * <h2>Template Variables:</h2>
 * <p>The email template expects these variables:</p>
 * <ul>
 *   <li><code>{{email}}</code> - Recipient's email address</li>
 *   <li><code>{{user_name}}</code> - User's display name</li>
 *   <li><code>{{passcode}}</code> - The 6-digit OTP code</li>
 *   <li><code>{{time}}</code> - OTP expiry timestamp</li>
 * </ul>
 * 
 * <h2>Graceful Degradation:</h2>
 * <p>If email sending fails for any reason, the OTP is logged to console
 * for testing/debugging purposes.</p>
 * 
 * @author Mini-Instapay Team
 * @version 1.0
 * @see OTPService
 */
public class EmailService {
    
    /** Singleton instance of the service */
    private static EmailService instance;
    
    /** EmailJS service identifier */
    private final String serviceId;
    
    /** EmailJS email template identifier */
    private final String templateId;
    
    /** EmailJS public API key (client-side) */
    private final String publicKey;
    
    /** EmailJS private key (server-side authentication) */
    private final String privateKey;
    
    /** Flag to enable/disable email sending (for testing) */
    private final boolean enableEmail;
    
    /** HTTP client for making API requests */
    private final HttpClient httpClient;
    
    /**
     * Private constructor to enforce Singleton pattern.
     * 
     * <p>Initializes EmailJS configuration and creates HTTP client
     * with a 10-second connection timeout.</p>
     */
    private EmailService() {
        this.enableEmail = true;
        
        // EmailJS Configuration - Replace with your own credentials
        this.serviceId = "service_t0ffn6m";
        this.templateId = "template_z77nyti";
        this.publicKey = "UMSyUDHVQrxmsGx6R";
        this.privateKey = "erpGM1ZSjCYMdS4bOcmLC";
        
        // Create HTTP client with connection timeout for API calls
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    /**
     * Returns the singleton instance of EmailService.
     * 
     * <p>Thread-safe implementation using synchronized keyword.</p>
     * 
     * @return The singleton instance of EmailService
     */
    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    /**
     * Sends an OTP email to the specified recipient.
     * 
     * <p>This method:</p>
     * <ol>
     *   <li>Validates that email sending is enabled</li>
     *   <li>Validates EmailJS credentials are configured</li>
     *   <li>Validates recipient email is not empty</li>
     *   <li>Builds JSON payload with template parameters</li>
     *   <li>Sends HTTP POST request to EmailJS API</li>
     *   <li>Returns success/failure status</li>
     * </ol>
     * 
     * <p><b>Fallback:</b> If email sending fails, the OTP is printed to
     * console for testing purposes.</p>
     * 
     * @param toEmail The recipient's email address
     * @param userName The recipient's display name (used in email greeting)
     * @param otp The One-Time Password to include in the email
     * @return {@code true} if email was sent successfully, {@code false} otherwise
     */
    public boolean sendOTPEmail(String toEmail, String userName, String otp) {
        // Check if email sending is disabled (for testing)
        if (!enableEmail) {
            System.out.println("Email sending is disabled. OTP for " + userName + ": " + otp);
            return true;
        }
        
        // Validate EmailJS credentials are configured
        if (serviceId == null || templateId == null || publicKey == null || 
            serviceId.isEmpty() || templateId.isEmpty() || publicKey.isEmpty() ||
            serviceId.equals("YOUR_SERVICE_ID")) {
            System.err.println("EmailJS credentials not configured. Please set Service ID.");
            System.err.println("Get Service ID from: https://dashboard.emailjs.com/admin/integration");
            System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
            return false;
        }
        
        // Validate recipient email address
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Recipient email address is empty. Cannot send OTP email.");
            System.out.println("OTP for " + userName + ": " + otp);
            return false;
        }
        
        try {
            // Calculate expiry time (15 minutes from now as per template)
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
            String expiryTimeStr = expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Build JSON payload for EmailJS API
            // Template uses: {{passcode}} and {{time}}
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"service_id\":").append(escapeJsonValue(serviceId)).append(",");
            jsonBuilder.append("\"template_id\":").append(escapeJsonValue(templateId)).append(",");
            jsonBuilder.append("\"user_id\":").append(escapeJsonValue(publicKey)).append(",");
            
            // Add accessToken (private key) for non-browser applications
            // This is required for server-side API calls
            if (privateKey != null && !privateKey.isEmpty() && !privateKey.equals("YOUR_EMAILJS_PRIVATE_KEY")) {
                jsonBuilder.append("\"accessToken\":").append(escapeJsonValue(privateKey)).append(",");
            }
            
            // Add template parameters
            jsonBuilder.append("\"template_params\":{");
            jsonBuilder.append("\"email\":").append(escapeJsonValue(toEmail)).append(",");
            jsonBuilder.append("\"user_name\":").append(escapeJsonValue(userName)).append(",");
            jsonBuilder.append("\"passcode\":").append(escapeJsonValue(otp)).append(",");
            jsonBuilder.append("\"time\":").append(escapeJsonValue(expiryTimeStr));
            jsonBuilder.append("}");
            jsonBuilder.append("}");
            String jsonPayload = jsonBuilder.toString();
            
            // Create HTTP POST request to EmailJS API
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(30))
                .build();
            
            // Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Check response status (2xx indicates success)
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("OTP email sent successfully to " + toEmail);
                System.out.println("Response: " + response.body());
                return true;
            } else {
                // API returned an error status
                System.err.println("Failed to send OTP email. Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
                return false;
            }
            
        } catch (Exception e) {
            // Network or other error occurred
            System.err.println("Failed to send OTP email: " + e.getMessage());
            e.printStackTrace();
            System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
            return false;
        }
    }
    
    /**
     * Escapes a string for safe inclusion in a JSON payload.
     * 
     * <p>Handles special characters that need escaping in JSON:</p>
     * <ul>
     *   <li>Backslash (\) → \\</li>
     *   <li>Double quote (") → \"</li>
     *   <li>Newline → \n</li>
     *   <li>Carriage return → \r</li>
     *   <li>Tab → \t</li>
     * </ul>
     * 
     * @param text The string to escape
     * @return JSON-safe string wrapped in double quotes, or "null" if input is null
     */
    private String escapeJsonValue(String text) {
        if (text == null) return "null";
        return "\"" + text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            + "\"";
    }
    
    /**
     * Checks if email sending is enabled.
     * 
     * @return {@code true} if email sending is enabled, {@code false} otherwise
     */
    public boolean isEmailEnabled() {
        return enableEmail;
    }
}
