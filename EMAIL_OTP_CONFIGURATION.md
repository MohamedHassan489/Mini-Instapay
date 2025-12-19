# Email-Based OTP Configuration Guide

## Overview
The 2FA system now sends OTP codes via email instead of displaying them in a dialog. This provides better security as the OTP is sent to the user's registered email address.

## How It Works

1. User logs in with username and password
2. If 2FA is enabled, system generates a 6-digit OTP
3. OTP is sent to user's registered email address
4. User enters OTP from email to complete login
5. OTP expires after 5 minutes

## Email Configuration

### Option 1: Using Gmail SMTP (Recommended for Development)

1. **Enable 2-Step Verification** on your Gmail account
2. **Generate an App Password**:
   - Go to Google Account Settings
   - Security â†’ 2-Step Verification â†’ App passwords
   - Generate a new app password for "Mail"
   - Copy the 16-character password

3. **Set Environment Variables**:
   `ash
   # Windows (PowerShell)
   ="your-email@gmail.com"
   ="your-app-password"
   
   # Windows (Command Prompt)
   set SMTP_USERNAME=your-email@gmail.com
   set SMTP_PASSWORD=your-app-password
   
   # Linux/Mac
   export SMTP_USERNAME="your-email@gmail.com"
   export SMTP_PASSWORD="your-app-password"
   `

### Option 2: Using Other SMTP Servers

To use a different SMTP server, modify EmailService.java:

`java
private EmailService() {
    this.smtpHost = "smtp.yourdomain.com";  // Your SMTP server
    this.smtpPort = "587";                  // Your SMTP port
    this.smtpUsername = System.getenv("SMTP_USERNAME");
    this.smtpPassword = System.getenv("SMTP_PASSWORD");
    this.fromEmail = System.getenv("SMTP_USERNAME");
}
`

### Option 3: Testing Without Email Server

If SMTP credentials are not configured, the system will:
- Print OTP to console for testing
- Still allow OTP verification
- Show appropriate messages to user

## Email Template

The OTP email includes:
- Personalized greeting with user's name
- 6-digit OTP code
- Expiry information (5 minutes)
- Security warnings
- Bank branding

## Testing

### Test Without Email Configuration:
1. Run the application without setting environment variables
2. System will print OTP to console
3. Check console output for OTP code
4. Enter OTP in the dialog

### Test With Email Configuration:
1. Set SMTP_USERNAME and SMTP_PASSWORD environment variables
2. Run the application
3. Login with 2FA enabled user
4. Check email inbox for OTP
5. Enter OTP in the dialog

## Troubleshooting

### Email Not Sending:
- Check environment variables are set correctly
- Verify SMTP credentials are valid
- Check firewall/network settings
- For Gmail: Ensure App Password is used (not regular password)
- Check console for error messages

### OTP Not Received:
- Check spam/junk folder
- Verify email address in user profile is correct
- Check SMTP server logs
- Verify email service is enabled

## Security Notes

- OTP expires after 5 minutes
- OTP is single-use (deleted after verification)
- Email is sent to user's registered email only
- Never share OTP with anyone
- OTP is not stored in plain text

## Code Locations

- Security/EmailService.java - Email sending service
- Security/OTPService.java - OTP generation and email integration
- Controllers/LoginController.java - Login flow with email OTP

