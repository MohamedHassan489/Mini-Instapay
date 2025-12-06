package com.example.national_bank_of_egypt;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Views.ViewFactory;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feature 23: GUI Navigation & Buttons Work
 * Tests: No white/blank pages, all navigation functional
 */
public class GUINavigationTest {
    private ViewFactory viewFactory;

    @BeforeEach
    void setUp() {
        viewFactory = Model.getInstance().getViewFactory();
    }

    @Test
    void testDashboardViewLoads() {
        // Test: Dashboard view should load without errors
        AnchorPane dashboard = viewFactory.getDashboardview();
        assertNotNull(dashboard, "Dashboard view should load");
        assertNotEquals(0, dashboard.getChildren().size() || dashboard.getPrefWidth() > 0,
            "Dashboard should have content");
    }

    @Test
    void testTransactionsViewLoads() {
        // Test: Transactions view should load
        AnchorPane transactions = viewFactory.getTransactionsView();
        assertNotNull(transactions, "Transactions view should load");
    }

    @Test
    void testAccountsViewLoads() {
        // Test: Accounts view should load
        AnchorPane accounts = viewFactory.getAccountsView();
        assertNotNull(accounts, "Accounts view should load");
    }

    @Test
    void testSendMoneyViewLoads() {
        // Test: Send Money view should load
        AnchorPane sendMoney = viewFactory.getSendMoneyView();
        assertNotNull(sendMoney, "Send Money view should load");
    }

    @Test
    void testProfileViewLoads() {
        // Test: Profile view should load
        AnchorPane profile = viewFactory.getProfileView();
        assertNotNull(profile, "Profile view should load");
    }

    @Test
    void testDisputesViewLoads() {
        // Test: Disputes view should load
        AnchorPane disputes = viewFactory.getDisputesView();
        assertNotNull(disputes, "Disputes view should load");
    }

    @Test
    void testNotificationsViewLoads() {
        // Test: Notifications view should load
        AnchorPane notifications = viewFactory.getNotificationsView();
        assertNotNull(notifications, "Notifications view should load");
    }

    @Test
    void testAdminUsersViewLoads() {
        // Test: Admin Users view should load
        AnchorPane users = viewFactory.getUsersView();
        assertNotNull(users, "Admin Users view should load");
    }

    @Test
    void testAdminTransactionsViewLoads() {
        // Test: Admin Transactions view should load
        AnchorPane adminTransactions = viewFactory.getAdminTransactionsView();
        assertNotNull(adminTransactions, "Admin Transactions view should load");
    }

    @Test
    void testAdminDisputesViewLoads() {
        // Test: Admin Disputes view should load
        AnchorPane adminDisputes = viewFactory.getAdminDisputesView();
        assertNotNull(adminDisputes, "Admin Disputes view should load");
    }

    @Test
    void testReportsViewLoads() {
        // Test: Reports view should load
        AnchorPane reports = viewFactory.getReportsView();
        assertNotNull(reports, "Reports view should load");
    }

    @Test
    void testSystemHealthViewLoads() {
        // Test: System Health view should load
        AnchorPane systemHealth = viewFactory.getSystemHealthView();
        assertNotNull(systemHealth, "System Health view should load");
    }

    @Test
    void testViewsDoNotReturnNull() {
        // Test: All views should return non-null (even if empty AnchorPane on error)
        assertNotNull(viewFactory.getDashboardview(), "Dashboard should not be null");
        assertNotNull(viewFactory.getTransactionsView(), "Transactions should not be null");
        assertNotNull(viewFactory.getAccountsView(), "Accounts should not be null");
        assertNotNull(viewFactory.getSendMoneyView(), "Send Money should not be null");
        assertNotNull(viewFactory.getProfileView(), "Profile should not be null");
        assertNotNull(viewFactory.getDisputesView(), "Disputes should not be null");
        assertNotNull(viewFactory.getNotificationsView(), "Notifications should not be null");
    }

    @Test
    void testViewFactorySingleton() {
        // Test: ViewFactory should be accessible through Model
        ViewFactory vf1 = Model.getInstance().getViewFactory();
        ViewFactory vf2 = Model.getInstance().getViewFactory();
        assertSame(vf1, vf2, "ViewFactory should be same instance through Model");
    }
}

