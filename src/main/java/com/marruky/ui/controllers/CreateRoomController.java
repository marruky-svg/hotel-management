package com.marruky.ui.controllers;

import com.marruky.factory.RoomFactory;
import com.marruky.model.person.Person;
import com.marruky.model.room.Room;
import com.marruky.repository.RoomRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateRoomController {

    @FXML
    private TextField numberField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField nightPriceField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField floorField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label errorLabel;

    private final RoomRepository roomRepository = new RoomRepository();
    private Person currentPerson;

    public void setCurrentPerson(Person person) {
        this.currentPerson = person;
    }

    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(
                "DOUBLE", "SINGLE", "SUITE", "PRESIDENTIAL"
        ));
    }

    @FXML
    public void handleCreate() {
        if (isAnyFieldEmpty()) {
            errorLabel.setText("Please fill all fields");
            return;
        }

        try {
            double nightPrice = Double.parseDouble(nightPriceField.getText());
            int capacity = Integer.parseInt(capacityField.getText());
            int floor = Integer.parseInt(floorField.getText());

            var room = RoomFactory.create(
                    0,
                    numberField.getText(),
                    Room.Type.valueOf(typeComboBox.getValue()),
                    nightPrice,
                    descriptionField.getText(),
                    floor,
                    capacity,
                    true
            );

            roomRepository.save(room);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Room");
            alert.setContentText("Room Created Successfully");
            alert.showAndWait();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manager.fxml"));
            Scene scene = new Scene(loader.load());
            ManagerController controller = loader.getController();
            controller.setCurrentPerson(currentPerson);
            Stage stage = (Stage) numberField.getScene().getWindow();
            stage.setScene(scene);

        } catch (NumberFormatException e) {
            errorLabel.setText("Price, capacity and floor must ne numbers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleBack(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/manager.fxml"));
            Scene scene = new Scene(loader.load());
            ManagerController controller = loader.getController();
            controller.setCurrentPerson(currentPerson);
            Stage stage = (Stage) numberField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isAnyFieldEmpty() {
        return numberField.getText().isBlank()
                || typeComboBox.getValue() == null
                || nightPriceField.getText().isBlank()
                || capacityField.getText().isBlank()
                || floorField.getText().isBlank()
                || descriptionField.getText().isBlank();
    }
}
