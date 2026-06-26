package com.marruky.ui.controllers;

import com.marruky.model.person.Person;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerController extends ReceptionistController{


    @FXML
    public void handleCreateRoom(){

    }

    @FXML
    public void handleViewReports(){

    }
    @FXML
    public void handleCreateStaff() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/createStaff.fxml"));
            Scene scene = new Scene(loader.load());
            CreateStaffController controller = loader.getController();
            controller.setCurrentPerson(currentPerson);
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
