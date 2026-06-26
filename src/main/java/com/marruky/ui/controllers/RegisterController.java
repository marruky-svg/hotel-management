package com.marruky.ui.controllers;

import com.marruky.exception.AuthException;
import com.marruky.exception.DataBaseException;
import com.marruky.model.User;
import com.marruky.model.person.Person;
import com.marruky.repository.PersonRepository;
import com.marruky.repository.UserRepository;
import com.marruky.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private TextField nifField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    public void handleRegister() {
        try {

            AuthService authService = new AuthService(userRepository);
            User user = authService.registerClient(
                    usernameField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    contactField.getText(),
                    nifField.getText()
            );

            Person person = new PersonRepository().findByUserId(user.getId());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/client.fxml"));
                Scene scene = new Scene(loader.load());
                ClientController controller = loader.getController();
                controller.setCurrentPerson(person);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (AuthException e) {
            errorLabel.setText(e.getMessage());
        } catch (DataBaseException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
