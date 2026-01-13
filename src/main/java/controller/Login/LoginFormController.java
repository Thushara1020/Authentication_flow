package controller.Login;

import controller.WelcomeFormController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {

    @FXML private Button btnSignIn;
    @FXML private Hyperlink btnSignUp;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    @FXML
    void SingInOnAction(ActionEvent event) {

        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (!email.toLowerCase().endsWith("@gmail.com")) {
            new Alert(Alert.AlertType.ERROR,
                    "Invalid Email! Please use a @gmail.com address.").show();
            return;
        }

        // 🔹 Get first name from DB
        String firstName = authenticateUserAndGetName(email, password);

        if (firstName != null) {

            // ✅ store logged user first name (STATIC)
            WelcomeFormController.loggedUserFirstName = firstName;

            navigateToDashboard();

        } else {
            new Alert(Alert.AlertType.ERROR,
                    "User not found or Invalid credentials! Please Sign Up.").show();
        }
    }

    // 🔹 Authenticate + return first_name
    private String authenticateUserAndGetName(String email, String password) {

        String url = "jdbc:mysql://localhost:3306/user_management?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = "TB20010415";

        String sql = "SELECT first_name FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pstm = connection.prepareStatement(sql)) {

            pstm.setString(1, email);
            pstm.setString(2, password);

            ResultSet rs = pstm.executeQuery();

            if (rs.next()) {
                return rs.getString("first_name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database connection error!").show();
        }

        return null;
    }

    private void navigateToDashboard() {
        try {
            Stage stage = (Stage) btnSignIn.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/WelcomeForm.fxml"))
            ));
            stage.setTitle("Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void SignUpOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnSignUp.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/SignInForm.fxml"))
            ));
            stage.setTitle("Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
