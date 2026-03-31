package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import service.AuthService;
import service.StaffService;

public class StaffController{

    @FMXL private TextField txtUsername;
    @FMXL private TextField txtFirstname;
    @FMXL private TextField txtLastname;
    @FMXL private PasswordField txtPassword;
    @FMXL private ComboBox<String> cmbRole;
    @FMXL private TextField txtUserId;
    @FMXL private Label lblStatus;

    private StaffService staffService = new StaffService();
    private AuthService authService = AuthService.getInstance();

    @FMXL
    public void initialize(){cmbRole.getItems().addAll("Receptionist","Cleaning Staff");
    }

    @FMXL
    public void handleCreateStaff() {
        if (!isManager()) return;

        User user = buildUserFromForm();

        if (user == null) return;

        boolean success = staffService.createStaffAccount(user);
        lblStatus.setText(success ? "Staff account created." : "Failed, username may be already existing.");
    }

    @FMXL
    private void handleUpdateStaff(){
        if(!isManager()) return;

        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()){
            lblStatus.setText("Enter a User ID to update.");
            return;
        }

        int userId = Integer.parseInt(idText);
        User updates=buildUserFromForm();
        if(updates== null)return;

        boolean success = staffService.updateStaffAccount(userId,updates);
        lblStatus.setText(success ? "Staff account updated." : "Failed to update");
    }

    @FMXL
    private void handleDeleteStaff(){
        if(!isManager()) return;

        String idText = txtUserId.getText().trim();
        if (idText.isEmpty()){
            lblStatus.setText("Enter a User ID to delete.");
            return;
        }

        int userId = Integer.parseInt(idText);
        User updates=buildUserFromForm();

        boolean success = staffService.deleteStaffAccount(userId);
        lblStatus.setText(success ? "Staff account deleted." : "Failed to delete");
    }

    private User buildUserFromForm(){
        String username = txt.Username.getText().trim();
        String firstname = txt.Firstname.getText().trim();
        String lastname = txt.Lastname.getText().trim();
        String password = txt.Password.getText().trim();
        String role = cmbRole.getValue();

        if(username.isEmpty() || firstname.isEmpty() || password.isEmpty() || role == null){
            lblStatus.setText("Please fill in all fields");
            return null;
        }

        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPasswordHash(password);
        user.setRole(role);
        return user;
    }

    private boolean isManager(){
        if(!"Manager".equals(authService.getCurrentRole())){
            lblStatus.setText("Access denied. Only Manager is allowed");
            return false;
        }
        return true;
    }
}