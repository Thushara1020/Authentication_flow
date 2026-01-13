package controller.SignIn;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignInFormController {

    @FXML private Button btnBackToLogin;
    @FXML private Button btnRegister;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;

    @FXML
    void registerOnAction(ActionEvent event) {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields!");
            return;
        }

        if (!email.toLowerCase().endsWith("@gmail.com")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please use a @gmail.com address.");
            return;
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        if (!password.matches(passwordRegex)) {
            showAlert(Alert.AlertType.ERROR, "Weak Password",
                    "Password must be 8+ chars, include Uppercase, Lowercase, and a Symbol.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Mismatch", "Passwords do not match!");
            return;
        }

        String hashedPassword = hashPassword(password);

        if (saveUser(firstName, lastName, email, hashedPassword)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration Successful! Please Login.");
            backToLoginOnAction(event);
        }
    }

    private boolean saveUser(String fName, String lName, String email, String hashedPassword) {
        String url = "jdbc:mysql://localhost:3306/user_management?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = "TB20010415";

        String sql = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
                 PreparedStatement pstm = connection.prepareStatement(sql)) {

                pstm.setString(1, fName);
                pstm.setString(2, lName);
                pstm.setString(3, email);
                pstm.setString(4, hashedPassword);

                return pstm.executeUpdate() > 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "DB Error", "Failed to save user: " + e.getMessage());
            return false;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error hashing password", ex);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    void backToLoginOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnBackToLogin.getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))));
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}