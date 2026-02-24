package controller;

import dao.UserDAO;
import model.Person;
import util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller for Profile editing screen.
 * 
 * INHERITANCE: Extends BaseController
 * POLYMORPHISM: Overrides abstract methods from BaseController
 * ENCAPSULATION: Private fields with controlled access
 */
public class ProfileController extends BaseController {

    @FXML
    private Label usernameDisplayLabel;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private Label infoLabel;

    // ========== POLYMORPHISM: Override abstract methods from BaseController
    // ==========

    @Override
    public void setUsername(String username) {
        this.currentUsername = username;
        usernameDisplayLabel.setText(username);
        loadCurrentProfile();
    }

    @Override
    protected Label getUsernameLabel() {
        return usernameDisplayLabel;
    }

    @Override
    protected String getScreenTitle() {
        return "Edit Profile";
    }

    @FXML
    public void initialize() {
        clearInfoLabel(infoLabel);
    }

    /**
     * Loads current profile data from database.
     */
    private void loadCurrentProfile() {
        Person user = UserDAO.getUserInfo(currentUsername);
        if (user != null) {
            addressField.setText(user.getAddress() != null ? user.getAddress() : "");
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        }
    }

    /**
     * Handles save button click.
     * Validates input and updates profile in database.
     */
    @FXML
    private void handleSave() {
        clearInfoLabel(infoLabel);

        String address = emptyToNull(addressField.getText());
        String phone = emptyToNull(phoneField.getText());

        // Address validation
        if (address != null && !ValidationUtil.isValidAddress(address)) {
            showInfoLabel(infoLabel,
                    "Address must contain at least one letter (e.g., 'Maltepe 111' is valid, '111' is not).", true);
            return;
        }

        // Phone validation using ValidationUtil (Turkish phone format)
        if (phone != null && !ValidationUtil.isValidPhoneNumber(phone)) {
            showInfoLabel(infoLabel, "Invalid phone format. Use: 05XXXXXXXXX, +905XXXXXXXXX, or 5XXXXXXXXX", true);
            return;
        }

        // Check if phone already exists (excluding current user)
        if (phone != null) {
            // Get current user's phone to compare
            Person currentUser = UserDAO.getUserInfo(currentUsername);
            String currentPhone = currentUser != null ? currentUser.getPhone() : null;

            // Only check if phone changed
            if (!phone.equals(currentPhone) && UserDAO.phoneExists(phone)) {
                showInfoLabel(infoLabel, "This phone number is already registered to another account.", true);
                return;
            }
        }

        // Update in database
        boolean success = UserDAO.updateProfileByUsername(currentUsername, address, phone);

        if (success) {
            showInfoLabel(infoLabel, "Profile updated ✅", false);
        } else {
            showInfoLabel(infoLabel, "Update failed!", true);
        }
    }

    /**
     * Handles cancel button click.
     * Closes the profile window.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) addressField.getScene().getWindow();
        stage.close();
    }
}
