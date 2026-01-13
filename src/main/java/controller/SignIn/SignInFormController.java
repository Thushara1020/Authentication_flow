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
            new Alert(Alert.AlertType.ERROR, "Please fill in all required fields!").show();
            return;
        }

        if (!email.toLowerCase().endsWith("@gmail.com")) {
            new Alert(Alert.AlertType.ERROR, "Invalid Email! Please use a @gmail.com address.").show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            new Alert(Alert.AlertType.ERROR, "Passwords do not match!").show();
            return;
        }

        if (saveUser(firstName, lastName, email, password)) {
            new Alert(Alert.AlertType.INFORMATION, "Registration Successful! Please Login.").show();
            backToLoginOnAction(event);
        }
    }

    private boolean saveUser(String fName, String lName, String email, String password) {

        String url = "jdbc:mysql://localhost:3306/user_management?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String dbPassword = "TB20010415";

        String sql = "INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, user, dbPassword);
            PreparedStatement pstm = connection.prepareStatement(sql);

            pstm.setString(1, fName);
            pstm.setString(2, lName);
            pstm.setString(3, email);
            pstm.setString(4, password);

            return pstm.executeUpdate() > 0;

        } catch (SQLException e) {

            if (e.getMessage().contains("Duplicate")) {
                new Alert(Alert.AlertType.ERROR, "This email is already registered!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Database Error!").show();
                e.printStackTrace();
            }
            return false;

        } catch (ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "MySQL Driver not found!").show();
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    void backToLoginOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnBackToLogin.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))
            ));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
