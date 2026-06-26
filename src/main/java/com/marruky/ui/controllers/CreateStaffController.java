package com.marruky.ui.controllers;


import com.marruky.model.person.Person;
import com.marruky.repository.UserRepository;
import com.marruky.service.AuthService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateStaffController {



    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private TextField nifField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField positionField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<String> shiftComboBox;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label errorLabel;

    private Person currentPerson;

    public void setCurrentPerson(Person person) {
        this.currentPerson = person;
    }

    @FXML
    public void initialize() {
        shiftComboBox.setItems(FXCollections.observableArrayList(
                "MORNING", "AFTERNOON", "NIGHT"
        ));

        typeComboBox.setItems(FXCollections.observableArrayList(
                "RECEPTIONIST", "MANAGER"
        ));
    }


    @FXML
    public void handleCreate() {

        if(isAnyFieldEmpty()){
            errorLabel.setText("Please fill all fields");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryField.getText());
            AuthService auth = new AuthService(new UserRepository());
            auth.registerStaff(
                    usernameField.getText(),
                    passwordField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    contactField.getText(),
                    nifField.getText(),
                    positionField.getText(),
                    salary,
                    shiftComboBox.getValue(),
                    typeComboBox.getValue()
                    );
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Register");
            alert.setContentText("Successful created");
            alert.showAndWait();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manager.fxml"));
                Scene scene = new Scene(loader.load());
                ManagerController controller = loader.getController();
                controller.setCurrentPerson(currentPerson);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
            }catch (IOException e){
                throw new RuntimeException(e);
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Salary must be a number");
            return;
        }

    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manager.fxml"));
            Scene scene = new Scene(loader.load());
            ManagerController controller = loader.getController();
            controller.setCurrentPerson(currentPerson);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAnyFieldEmpty() {
        return nameField.getText().isBlank() ||
                emailField.getText().isBlank() ||
                contactField.getText().isBlank() ||
                nifField.getText().isBlank() ||
                usernameField.getText().isBlank() ||
                passwordField.getText().isBlank() ||
                positionField.getText().isBlank() ||
                salaryField.getText().isBlank() ||
                shiftComboBox.getValue() == null ||
                typeComboBox.getValue() == null;
    }

}
