package com.example.national_bank_of_egypt.Controllers.Client;

import com.example.national_bank_of_egypt.Models.Model;
import com.example.national_bank_of_egypt.Utils.AnimationUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;
    private Node currentView = null;

    /**
     * Helper method to transition between views with smooth animation
     */
    private void transitionToView(Node newView) {
        if (client_parent == null || newView == null) {
            return;
        }

        Node oldView = currentView;
        currentView = newView;

        if (oldView != null && oldView.getOpacity() > 0) {
            // Fade out old view, then fade in new view
            // Fade out old view, then fade in new view
            var fadeOutTransition = AnimationUtils.fadeOut(oldView, AnimationUtils.PAGE_TRANSITION_DURATION);
            fadeOutTransition.setOnFinished(e -> {
                // CRITICAL FIX: Check if the user has switched views again while animation was
                // running
                if (currentView == newView) {
                    client_parent.setCenter(newView);
                    newView.setOpacity(0);
                    AnimationUtils.fadeIn(newView, AnimationUtils.PAGE_TRANSITION_DURATION).play();
                }
            });
            fadeOutTransition.play();
        } else {
            // No old view, just fade in new view
            newView.setOpacity(0);
            client_parent.setCenter(newView);
            AnimationUtils.fadeIn(newView, AnimationUtils.PAGE_TRANSITION_DURATION).play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up listener first
        Model.getInstance().getViewFactory().getClientSelectedMenuItem()
                .addListener((observableValue, oldVal, newVal) -> {
                    if (client_parent == null) {
                        System.err.println("Warning: client_parent is null in ClientController listener");
                        return;
                    }
                    switch (newVal) {
                        case DASHBOARD -> {
                            var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                            if (dashboard != null) {
                                transitionToView(dashboard);
                            } else {
                                System.err.println("Error: Dashboard view is null");
                                showErrorView("Error loading Dashboard view. Please check the console for details.");
                            }
                        }
                        case TRANSACTIONS -> {
                            var view = Model.getInstance().getViewFactory().getTransactionsView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Transactions view is null");
                                showErrorView("Error loading Transactions view. Please check the console for details.");
                            }
                        }
                        case ACCOUNTS -> {
                            var view = Model.getInstance().getViewFactory().getAccountsView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Accounts view is null");
                                showErrorView("Error loading Accounts view. Please check the console for details.");
                            }
                        }
                        case SEND_MONEY -> {
                            var view = Model.getInstance().getViewFactory().getSendMoneyView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Send Money view is null");
                                showErrorView("Error loading Send Money view. Please check the console for details.");
                            }
                        }
                        case PROFILE -> {
                            var view = Model.getInstance().getViewFactory().getProfileView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Profile view is null");
                                showErrorView("Error loading Profile view. Please check the console for details.");
                            }
                        }
                        case DISPUTES -> {
                            var view = Model.getInstance().getViewFactory().getDisputesView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Disputes view is null");
                                showErrorView("Error loading Disputes view. Please check the console for details.");
                            }
                        }
                        case NOTIFICATIONS -> {
                            var view = Model.getInstance().getViewFactory().getNotificationsView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Notifications view is null");
                                showErrorView(
                                        "Error loading Notifications view. Please check the console for details.");
                            }
                        }
                        case BANK_STATEMENT -> {
                            var view = Model.getInstance().getViewFactory().getBankStatementView();
                            if (view != null) {
                                transitionToView(view);
                            } else {
                                System.err.println("Error: Bank Statement view is null");
                                showErrorView(
                                        "Error loading Bank Statement view. Please check the console for details.");
                            }
                        }
                        default -> {
                            var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                            if (dashboard != null) {
                                transitionToView(dashboard);
                            } else {
                                showErrorView("Error loading Dashboard view. Please check the console for details.");
                            }
                        }
                    }
                });

        // Set default view to Dashboard immediately
        if (client_parent != null) {
            try {
                System.out.println("ClientController: Initializing, loading dashboard...");
                var dashboard = Model.getInstance().getViewFactory().getDashboardview();
                if (dashboard != null) {
                    System.out.println("ClientController: Dashboard loaded, setting as center view");
                    // Set dashboard immediately - make it visible
                    dashboard.setOpacity(1.0);
                    client_parent.setCenter(dashboard);
                    currentView = dashboard;
                    System.out.println(
                            "ClientController: Dashboard set successfully, currentView = " + (currentView != null));

                    // Animate it with fade-in after a short delay to ensure it's visible first
                    Platform.runLater(() -> {
                        if (dashboard != null && dashboard.getParent() != null) {
                            dashboard.setOpacity(0);
                            AnimationUtils.fadeIn(dashboard, AnimationUtils.ENTRANCE_DURATION).play();
                        }
                    });
                } else {
                    System.err.println("Error: Dashboard view is null during initialization");
                    showErrorView("Error loading Dashboard view. Please check the console for details.");
                }
                // Set the menu item to keep state consistent
                Model.getInstance().getViewFactory().getClientSelectedMenuItem()
                        .set(com.example.national_bank_of_egypt.Views.clientmenuoption.DASHBOARD);
            } catch (Exception e) {
                System.err.println("Error initializing ClientController: " + e.getMessage());
                e.printStackTrace();
                showErrorView("Error initializing client window: " + e.getMessage());
            }
        } else {
            System.err.println("Error: client_parent is null during initialization");
        }
    }

    /**
     * Show an error view when a page fails to load
     */
    private void showErrorView(String message) {
        if (client_parent == null)
            return;

        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20; -fx-wrap-text: true;");
        errorLabel.setWrapText(true);
        javafx.scene.layout.AnchorPane errorPane = new javafx.scene.layout.AnchorPane();
        errorPane.getChildren().add(errorLabel);
        javafx.scene.layout.AnchorPane.setLeftAnchor(errorLabel, 20.0);
        javafx.scene.layout.AnchorPane.setTopAnchor(errorLabel, 20.0);
        javafx.scene.layout.AnchorPane.setRightAnchor(errorLabel, 20.0);
        transitionToView(errorPane);
    }
}
