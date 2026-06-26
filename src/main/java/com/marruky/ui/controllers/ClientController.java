package com.marruky.ui.controllers;

import com.marruky.model.Reservation;
import com.marruky.model.person.Client;
import com.marruky.model.person.Person;
import com.marruky.model.room.Room;
import com.marruky.repository.*;
import com.marruky.service.InvoiceService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class ClientController {

    private Person currentPerson;
    private Client currentClient;

    private final ReservationRepository reservationRepository = new ReservationRepository();
    private final RoomRepository roomRepository = new RoomRepository();
    private final ServiceRepository serviceRepository = new ServiceRepository();
    private final InvoiceRepository invoiceRepository = new InvoiceRepository();
    private final PersonRepository personRepository = new PersonRepository();
    private final InvoiceService invoiceService = new InvoiceService();

    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableColumn<Reservation, Integer> idColumn;
    @FXML
    private TableColumn<Reservation, Integer> roomIdColumn;
    @FXML
    private TableColumn<Reservation, String> checkInColumn;
    @FXML
    private TableColumn<Reservation, String> checkOutColumn;
    @FXML
    private TableColumn<Reservation, String> stateColumn;

    @FXML
    private TableView<Room> roomsTable;
    @FXML
    private TableColumn<Room, Integer> roomIdTableColumn;
    @FXML
    private TableColumn<Room, String> roomNumberColumn;
    @FXML
    private TableColumn<Room, String> roomTypeColumn;
    @FXML
    private TableColumn<Room, Double> roomPriceColumn;
    @FXML
    private TableColumn<Room, Integer> roomCapacityColumn;

    public void initialize() {
        idColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        roomIdColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getRoomId()).asObject());
        checkInColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCheckInDate().toString()));
        checkOutColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCheckOutDate().toString()));
        stateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getState().toString()));

        roomIdTableColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        roomNumberColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNumber()));
        roomTypeColumn.setCellValueFactory((cell ->
                new SimpleStringProperty(cell.getValue().getType().toString())));
        roomPriceColumn.setCellValueFactory(cell ->
                new SimpleDoubleProperty(cell.getValue().getNightPrice()).asObject());
        roomCapacityColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getCapacity()).asObject());
    }


    public void setCurrentPerson(Person person) {
        this.currentPerson = person;
        this.currentClient = personRepository.findClientByPersonId(person.getId());
        reservationsTable.setItems(
                FXCollections.observableArrayList(
                        reservationRepository.findByClientId(currentClient.getId())
                )
        );
    }

    @FXML
    public void handleMyReservations() {
        reservationsTable.setItems(
                FXCollections.observableArrayList(
                        reservationRepository.findByClientId(currentClient.getId())
                )
        );
    }

    @FXML
    public void handleMakeReservation() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No room selected");

        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        TextInputDialog checkInDate = new TextInputDialog();
        checkInDate.setTitle("Check-in Date");
        checkInDate.setHeaderText("Choose Check-in Date(yyyy-MM-dd");
        Optional<String> checkInResult = checkInDate.showAndWait();

        TextInputDialog checkOutDate = new TextInputDialog();
        checkOutDate.setTitle("Check-Out Date");
        checkOutDate.setHeaderText("Choose Check-in Date(yyyy-MM-dd");
        Optional<String> checkOutResult = checkOutDate.showAndWait();

        if (checkInResult.isEmpty()) {
            alert.setContentText("Please choose a available Check-In Date");
            alert.showAndWait();
            return;
        }

        if (checkOutResult.isEmpty()) {
            alert.setContentText("Please choose a available Check-Out Date");
            alert.showAndWait();
            return;
        }

        if (Date.valueOf(checkOutResult.get()).before(Date.valueOf(checkInResult.get()))) {
            alert.setContentText("Please choose a available Check-Out Date");
            alert.showAndWait();
            return;
        }

        reservationRepository.save(
                new Reservation(
                        0,
                        currentClient.getId(),
                        selected.getId(),
                        0,
                        Date.valueOf(checkInResult.get()).toLocalDate(),
                        Date.valueOf(checkOutResult.get()).toLocalDate(),
                        0,
                        Reservation.State.PENDING,
                        null,
                        LocalDateTime.now()
                )
        );
        handleMyReservations();
    }

    @FXML
    public void handleAvailableRooms() {
        roomsTable.setItems(
                FXCollections.observableArrayList(
                        roomRepository.findAvailable()
                )
        );
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
