package ui;
//aljory
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import service.AuthService;
import service.StaffService;

public class StaffController{

    @FXML private TextField txtUsername;
    @FXML private TextField txtFirstname;
    @FXML private TextField txtLastname;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private TextField txtUserId;
    @FXML private Label lblStatus;

    private StaffService staffService = new StaffService();

    @FXML
    public void initialize(){
        cmbRole.getItems().addAll("Receptionist","Cleaning Staff");
    }

    @FXML
    public void handleCreateStaff() {
        if (!isManager()) return;

        String password = txtPassword.getText().trim();
        User user = buildUserFromForm();
        if (user == null) return;

        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.createStaffAccount(user, password, managerId);
        lblStatus.setText(success ? "Staff account created." : "Failed, username may be already existing.");
    }

    @FXML
    private void handleUpdateStaff(){
        if(!isManager()) return;

        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()){
            lblStatus.setText("Enter a User ID to update.");
            return;
        }

        int userId = Integer.parseInt(idText);
        User updates = buildUserFromForm();
        if(updates == null) return;

        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.updateStaffAccount(userId, updates, managerId);
        lblStatus.setText(success ? "Staff account updated." : "Failed to update");
    }

    @FXML
    private void handleDeleteStaff(){
        if(!isManager()) return;

        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()){
            lblStatus.setText("Enter a User ID to delete.");
            return;
        }

        int userId = Integer.parseInt(idText);
        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = staffService.deleteStaffAccount(userId, managerId);
        lblStatus.setText(success ? "Staff account deleted." : "Failed to delete");
    }

    private User buildUserFromForm(){
        String username  = txtUsername.getText().trim();
        String firstname = txtFirstname.getText().trim();
        String lastname  = txtLastname.getText().trim();
        String password  = txtPassword.getText().trim();
        String role      = cmbRole.getValue();

        if(username.isEmpty() || firstname.isEmpty() || password.isEmpty() || role == null){
            lblStatus.setText("Please fill in all fields");
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

    private boolean isManager(){
        if(!"Manager".equals(AuthService.getCurrentRole())){
            lblStatus.setText("Access denied. Only Manager is allowed");
            return false;
        }
        return true;
    }
}