package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeFormController implements Initializable {


    public static String loggedUserFirstName;

    @FXML
    private Button btnLogout;

    @FXML
    private Label txtFirstName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (loggedUserFirstName != null) {
            txtFirstName.setText("Welcome, " + loggedUserFirstName + " 👋");
        }
    }

    @FXML
    void LogoutOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"))
            ));
            stage.setTitle("Login");
            stage.show();

            // clear session
            loggedUserFirstName = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
