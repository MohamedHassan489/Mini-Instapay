# Mini-InstaPay Manual Test Checklist

## ✅ Application Startup Test

**Steps:**
1. Application launches
2. Login window appears (should NOT be white/blank)
3. Window title shows "Mini-InstaPay"
4. All UI elements visible:
   - Account type selector (Client/Admin)
   - Username field
   - Password field
   - Login button
   - Sign Up button (visible when Client selected)

**Expected Result:** ✅ Login window displays correctly, no white page

---

## ✅ User Registration Test

**Steps:**
1. On login screen, select "Client"
2. Click "Sign Up" button
3. Registration window opens (should NOT be white/blank)
4. Fill in all required fields:
   - First Name: "Test"
   - Last Name: "User"
   - Email: "test@example.com"
   - Phone Number: "1234567890"
   - Address: "123 Test St"
   - Username: "testuser"
   - Password: "password123"
   - Confirm Password: "password123"
5. Click "Register"

**Expected Result:** ✅ Registration successful, returns to login screen

---

## ✅ User Login Test

**Steps:**
1. On login screen, select "Client"
2. Enter username: "testuser"
3. Enter password: "password123"
4. Click "Log in"

**Expected Result:** ✅ Client dashboard opens (should NOT be white/blank)

---

## ✅ Client Dashboard Navigation Test

**Steps:**
1. After login, verify dashboard displays
2. Click each menu item:
   - Dashboard
   - Transactions
   - Accounts
   - Send Money
   - Profile
   - Disputes
   - Notifications

**Expected Result:** ✅ Each view loads without white pages, content displays

---

## ✅ Admin Login Test

**Steps:**
1. On login screen, select "Admin"
2. Enter admin credentials (if available)
3. Click "Log in"

**Expected Result:** ✅ Admin dashboard opens (should NOT be white/blank)

---

## ✅ Admin Dashboard Navigation Test

**Steps:**
1. After admin login, verify dashboard displays
2. Click each menu item:
   - Users
   - Transactions
   - Disputes
   - Reports
   - System Health

**Expected Result:** ✅ Each view loads without white pages, content displays

---

## ✅ Send Money Test

**Steps:**
1. Login as user
2. Navigate to "Send Money"
3. Fill in:
   - Receiver: (phone number or username)
   - Source Account: (select from dropdown)
   - Amount: "100.00"
   - Transaction Type: "INSTANT"
   - Message: "Test payment"
4. Click "Send Money"

**Expected Result:** ✅ Transaction processes or shows appropriate error message

---

## ✅ View Transactions Test

**Steps:**
1. Login as user
2. Navigate to "Transactions"
3. Verify transaction list displays

**Expected Result:** ✅ Transaction history shows (may be empty for new user)

---

## ✅ Admin View All Transactions Test

**Steps:**
1. Login as admin
2. Navigate to "Transactions"
3. Verify all transactions list displays

**Expected Result:** ✅ All transactions show in list

---

## ✅ Admin View Disputes Test

**Steps:**
1. Login as admin
2. Navigate to "Disputes"
3. Verify disputes list displays

**Expected Result:** ✅ All disputes show in list

---

## ✅ Error Handling Test

**Steps:**
1. Try to login with invalid credentials
2. Try to register with duplicate username
3. Try invalid operations

**Expected Result:** ✅ Appropriate error messages display, no crashes

---

## Issues to Report

If you encounter:
- ❌ White/blank pages
- ❌ Application crashes
- ❌ Missing UI elements
- ❌ Navigation not working
- ❌ Error dialogs instead of proper views
- ❌ Database errors

Please note the exact steps to reproduce.

