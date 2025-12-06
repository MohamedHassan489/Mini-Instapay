# Mini-InstaPay Feature Test Plan

## STEP 1: Feature List (23 Features)

### User Module (Features 1-6)
1. **User Registration** - Users can register with credentials
2. **User Login** - Users can log in with credentials and 2FA
3. **Add Bank Account** - Users can link/add bank accounts
4. **Remove Bank Account** - Users can remove linked accounts
5. **Update Bank Account** - Users can update bank account information
6. **Update Personal Information** - Users can update profile details

### Transactions Module (Features 7-11)
7. **Send Money** - Transfer money using phone/account number
8. **Receive Money** - Users can receive funds into linked accounts
9. **View Transaction History** - Detailed list of past transactions
10. **Request Refund / Open Dispute** - Users can request refunds or dispute transactions
11. **Set Transaction Limits & Approaching-Limit Notification** - Set daily/weekly limits with notifications

### Security Module (Features 12-14)
12. **Two-Factor Authentication (2FA)** - OTP/biometric verification
13. **Encryption Layer** - Secure communication using AES encryption
14. **Fraud Detection / Suspicious Activity Flagging** - Pattern-based fraud detection

### Notifications Module (Features 15-16)
15. **Push Notifications** - Notifications for transactions, balance, offers
16. **Transaction Alerts** - Real-time alerts for transfers and deposits

### Admin Module (Features 17-19)
17. **Admin – Manage Users** - View profiles, suspend accounts, handle disputes
18. **Admin – Oversee Transactions** - Monitor transactions, flag suspicious activities
19. **Admin – System Health Monitoring** - Dashboard with uptime, success rates, metrics

### Reports Module (Features 20-21)
20. **Reports – Transaction Summary** - Monthly and annual transaction reports
21. **Reports – Account Usage Analysis** - User engagement and usage trends

### System Requirements (Features 22-23)
22. **Design Patterns Present** - At least 3 design patterns (Strategy/Observer/Factory/Singleton/etc.)
23. **GUI Navigation & Buttons Work** - No white/blank pages, all navigation functional

---

## Test Implementation Strategy

- **Unit Tests**: Services, validation, fraud detection, limits, encryption
- **Integration Tests**: Database operations, transactions, authentication
- **Controller Tests**: Navigation, button actions, view loading

