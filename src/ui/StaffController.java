package ui;
//aljory and lyba did this
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import service.AuthService;
import service.StaffService;

public class StaffController {

    @FXML private TextField     txtUsername;
    @FXML private TextField     txtFirstname;
    @FXML private TextField     txtLastname;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private TextField     txtUserId;
    @FXML private Label         lblStatus;

    private StaffService staffService = new StaffService();

    @FXML
    public void initialize() {
        cmbRole.getItems().addAll("Receptionist", "Cleaning Staff");
    }

    // Navigation between Screens — Manager only
    @FXML private void goToStaff()         { /* already here */ }
    @FXML private void goToRooms()         { NavigationHelper.navigateTo(txtUsername, "/ui/RoomView.fxml"); }
    @FXML private void goToReviewRecords() { NavigationHelper.navigateTo(txtUsername, "/ui/ReviewRecordsView.fxml"); }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(txtUsername, "/ui/LoginView.fxml");
    }

    // Actions
    @FXML
    public void handleCreateStaff() {
        if (!isManager()) return;
        String password = txtPassword.getText().trim();
        User user = buildUserFromForm();
        if (user == null) return;
        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.createStaffAccount(user, password, managerId);
        lblStatus.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblStatus.setText(success ? "✓  Staff account created." : "✗  Failed — username may already exist.");
    }

    @FXML
    private void handleUpdateStaff() {
        if (!isManager()) return;
        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()) { lblStatus.setText("Enter a User ID to update."); return; }
        int userId;
        try { userId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { lblStatus.setText("User ID must be a number."); return; }
        User updates = buildUserFromForm();
        if (updates == null) return;
        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.updateStaffAccount(userId, updates, managerId);
        lblStatus.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblStatus.setText(success ? "✓  Staff account updated." : "✗  Failed to update.");
    }

    @FXML
    private void handleDeleteStaff() {
        if (!isManager()) return;
        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()) { lblStatus.setText("Enter a User ID to delete."); return; }
        int userId;
        try { userId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { lblStatus.setText("User ID must be a number."); return; }
        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.deleteStaffAccount(userId, managerId);
        lblStatus.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblStatus.setText(success ? "✓  Staff account deleted." : "✗  Failed to delete.");
    }

    //Helpers
    private User buildUserFromForm() {
        String username  = txtUsername.getText().trim();
        String firstname = txtFirstname.getText().trim();
        String lastname  = txtLastname.getText().trim();
        String password  = txtPassword.getText().trim();
        String role      = cmbRole.getValue();
        if (username.isEmpty() || firstname.isEmpty() || password.isEmpty() || role == null) {
            lblStatus.setText("Please fill in all fields.");
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setPasswordHash(password);
        user.setRole(role);
        return user;
    }

    private boolean isManager() {
        if (!"Manager".equals(AuthService.getCurrentRole())) {
            lblStatus.setText("Access denied. Only Manager is allowed.");
            return false;
        }
        return true;
    }
}