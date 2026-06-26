package com.marruky.ui.controllers;

import com.marruky.exception.AuthException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.User;
import com.marruky.model.person.Person;
import com.marruky.repository.PersonRepository;
import com.marruky.repository.UserRepository;
import com.marruky.service.AuthService;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService(new UserRepository());

    @FXML
    private void handleLogin(){
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(username, password);
            Person person = new PersonRepository().findByUserId(user.getId());

            FXMLLoader loader;
            Stage stage = (Stage) usernameField.getScene().getWindow();

            if(person.getType() == Person.Type.RECEPTIONIST) {
                loader = new FXMLLoader(getClass().getResource("/views/receptionist.fxml"));
                Scene scene = new Scene(loader.load());
                ReceptionistController controller = loader.getController();
                controller.setCurrentPerson(person);
                stage.setScene(scene);
            }else if(person.getType() == Person.Type.MANAGER) {
                loader = new FXMLLoader(getClass().getResource("/views/manager.fxml"));
                Scene scene = new Scene(loader.load());
                ManagerController controller = loader.getController();
                controller.setCurrentPerson(person);
                stage.setScene(scene);
            }
            else{
                loader = new FXMLLoader(getClass().getResource("/views/client.fxml"));
                Scene scene = new Scene(loader.load());
                ClientController controller = loader.getController();
                controller.setCurrentPerson(person);
                stage.setScene(scene);
            }

        }catch (AuthException e){
            errorLabel.setText(e.getMessage());
        }catch (NotFoundException e){
            errorLabel.setText("Username not found");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
