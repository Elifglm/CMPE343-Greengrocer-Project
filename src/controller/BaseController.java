package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Abstract base controller for all screens.
 * 
 * INHERITANCE: All controllers extend this class
 * POLYMORPHISM: Abstract methods must be implemented by subclasses
 * ENCAPSULATION: Protected fields and methods accessible to subclasses
 */
public abstract class BaseController {

    // ENCAPSULATION: Protected field accessible to subclasses
    protected String currentUsername;

    // ========== ABSTRACT METHODS (POLYMORPHISM) ==========

    /**
     * Must be implemented by subclasses to set username and initialize UI.
     * 
     * @param username The logged-in username
     */
    public abstract void setUsername(String username);

    /**
     * Returns the username label for the screen.
     * Used by common methods like performLogout().
     */
    protected abstract Label getUsernameLabel();

    /**
     * Returns the screen title for this controller.
     */
    protected abstract String getScreenTitle();

    // ========== COMMON METHODS (INHERITANCE) ==========

    /**
     * Common logout implementation.
     * Subclasses can override if needed, but default behavior navigates to login.
     */
    protected void performLogout() {
        try {
            Label label = getUsernameLabel();
            if (label == null || label.getScene() == null)
                return;

            Stage stage = (Stage) label.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 960, 540);

            applyStylesheet(scene);

            stage.setTitle("GreenGrocer Login");
            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Logout failed!");
        }
    }

    protected void navigateToScreen(String fxmlPath, String title, Object controller) {
        try {
            Label label = getUsernameLabel();
            if (label == null || label.getScene() == null)
                return;

            Stage stage = (Stage) label.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 960, 540);
            applyStylesheet(scene);

            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation failed!");
        }
    }

    /**
     * Apply modern stylesheet to scene.
     */
    protected void applyStylesheet(Scene scene) {
        try {
            String css = getClass().getResource("/view/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            // CSS not found, continue without
        }
    }

    /**
     * Show error message in an alert dialog.
     */
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success/information message in an alert dialog.
     */
    protected void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info message in a label with appropriate color.
     * 
     * @param label   The label to update
     * @param message The message to display
     * @param isError If true, displays in red; otherwise green
     */
    protected void showInfoLabel(Label label, String message, boolean isError) {
        if (label != null) {
            label.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
            label.setText(message);
        }
    }

    /**
     * Clear info label.
     */
    protected void clearInfoLabel(Label label) {
        if (label != null) {
            label.setText("");
        }
    }

    // ========== UTILITY METHODS (ENCAPSULATION) ==========

    /**
     * Safely trim a string, returning empty string if null.
     */
    protected static String safeTrim(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
     * Convert empty string to null.
     */
    protected static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    /**
     * Get current username.
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
}
