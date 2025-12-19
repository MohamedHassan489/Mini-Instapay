package com.example.national_bank_of_egypt.Security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailService {
    private static EmailService instance;
    private final String serviceId;
    private final String templateId;
    private final String publicKey;
    private final String privateKey;
    private final boolean enableEmail;
    private final HttpClient httpClient;
    
    private EmailService() {
        this.enableEmail = true;
        
        // EmailJS Configuration
        this.serviceId = "service_t0ffn6m";
        this.templateId = "template_z77nyti";
        this.publicKey = "UMSyUDHVQrxmsGx6R";
        this.privateKey = "erpGM1ZSjCYMdS4bOcmLC";
        
        // Create HTTP client for API calls
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    public boolean sendOTPEmail(String toEmail, String userName, String otp) {
        if (!enableEmail) {
            System.out.println("Email sending is disabled. OTP for " + userName + ": " + otp);
            return true;
        }
        
        if (serviceId == null || templateId == null || publicKey == null || 
            serviceId.isEmpty() || templateId.isEmpty() || publicKey.isEmpty() ||
            serviceId.equals("YOUR_SERVICE_ID")) {
            System.err.println("EmailJS credentials not configured. Please set Service ID.");
            System.err.println("Get Service ID from: https://dashboard.emailjs.com/admin/integration");
            System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
            return false;
        }
        
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Recipient email address is empty. Cannot send OTP email.");
            System.out.println("OTP for " + userName + ": " + otp);
            return false;
        }
        
        try {
            // Calculate expiry time (15 minutes from now as per template)
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);
            String expiryTimeStr = expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Create JSON payload for EmailJS API
            // Template uses: {{passcode}} and {{time}}
            // For non-browser applications, include accessToken (private key) in the payload
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"service_id\":").append(escapeJsonValue(serviceId)).append(",");
            jsonBuilder.append("\"template_id\":").append(escapeJsonValue(templateId)).append(",");
            jsonBuilder.append("\"user_id\":").append(escapeJsonValue(publicKey)).append(",");
            // Add accessToken (private key) for non-browser applications
            if (privateKey != null && !privateKey.isEmpty() && !privateKey.equals("YOUR_EMAILJS_PRIVATE_KEY")) {
                jsonBuilder.append("\"accessToken\":").append(escapeJsonValue(privateKey)).append(",");
            }
            jsonBuilder.append("\"template_params\":{");
            jsonBuilder.append("\"email\":").append(escapeJsonValue(toEmail)).append(",");
            jsonBuilder.append("\"user_name\":").append(escapeJsonValue(userName)).append(",");
            jsonBuilder.append("\"passcode\":").append(escapeJsonValue(otp)).append(",");
            jsonBuilder.append("\"time\":").append(escapeJsonValue(expiryTimeStr));
            jsonBuilder.append("}");
            jsonBuilder.append("}");
            String jsonPayload = jsonBuilder.toString();
            
            // Create HTTP request to EmailJS API
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(30))
                .build();
            
            // Send request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Check response status
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("OTP email sent successfully to " + toEmail);
                System.out.println("Response: " + response.body());
                return true;
            } else {
                System.err.println("Failed to send OTP email. Status: " + response.statusCode());
                System.err.println("Response: " + response.body());
                System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            e.printStackTrace();
            System.out.println("OTP for " + userName + " (" + toEmail + "): " + otp);
            return false;
        }
    }
    
    /**
     * Escape JSON string value for safe inclusion in JSON payload
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
    
    public boolean isEmailEnabled() {
        return enableEmail;
    }
}
