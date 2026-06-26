package com.marruky.ui.controllers;

import com.marruky.model.Reservation;
import com.marruky.model.room.Room;
import com.marruky.repository.*;
import com.marruky.service.InvoiceService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;


public class StaffController {


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
    private TableColumn<Reservation, Integer> clientIdColumn;
    @FXML
    private TableColumn<Reservation, Integer> roomIdColumn;
    @FXML
    private TableColumn<Reservation, String> checkInColumn;
    @FXML
    private TableColumn<Reservation, String> checkOutColumn;
    @FXML
    private TableColumn<Reservation, String> stateColumn;


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        clientIdColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getClientId()).asObject());
        roomIdColumn.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getRoomId()).asObject());
        checkInColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCheckInDate().toString()));
        checkOutColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCheckOutDate().toString()));
        stateColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getState().toString()));

    }

    @FXML
    public void handleListReservations() {
        reservationsTable.setItems(
                FXCollections.observableArrayList(reservationRepository.findAll())
        );
    }

    @FXML
    public void handleConfirmReservation() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");

        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        if (selected.getState() != Reservation.State.PENDING) {
            alert.setContentText("Reservation is not in PENDING state.");
            alert.showAndWait();
            return;
        }

        reservationRepository.updateState(selected.getId(), Reservation.State.CONFIRMED);
        handleListReservations();
    }

    @FXML
    public void handleCheckIn() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");

        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            alert.showAndWait();
            return;
        }

        if (selected.getState() == Reservation.State.CONFIRMED) {
            if (selected.getCheckInDate().equals(LocalDate.now())) {
                reservationRepository.updateState(selected.getId(), Reservation.State.CHECKED_IN);
                var roomOptional = roomRepository.findById(selected.getRoomId());
                if (roomOptional.isPresent()) {
                    var room = roomOptional.get();
                    room.setAvailable(false);
                    roomRepository.update(room);
                }
                handleListReservations();
            } else {
                alert.setContentText("Check.in date is not today");
                alert.showAndWait();
                return;
            }
        } else {
            alert.setContentText("Reservation is not in CONFIRMED state. ");
            alert.showAndWait();
            return;
        }

    }

    @FXML
    public void handleCheckOut() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");

        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        if (selected.getState() != Reservation.State.CHECKED_IN) {
            alert.setContentText("Reservation is not CHECKED_IN");
            alert.showAndWait();
            return;
        }

        reservationRepository.updateState(selected.getId(), Reservation.State.CHECKED_OUT);
        var roomOptional = roomRepository.findById(selected.getRoomId());
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setAvailable(true);
            roomRepository.update(room);
        }

        handleListReservations();
    }

    @FXML
    public void handleAddService() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");


        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        if (selected.getState() != Reservation.State.CHECKED_IN) {
            alert.setContentText("The reservation must be CHECKED_IN");
            alert.showAndWait();
            return;
        }

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Available Services");
        StringBuilder sb = new StringBuilder();
        for (var service : serviceRepository.findAll()) {
            sb.append(service.getId()).append(" - ")
                    .append(service.getName()).append(" (")
                    .append(service.getPrice()).append("€)\n");
        }
        info.setContentText(sb.toString());
        info.showAndWait();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Service");
        dialog.setHeaderText("Enter Service ID");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            TextInputDialog amount = new TextInputDialog();
            amount.setTitle("Amount");
            amount.setHeaderText("Enter The Amount");
            Optional<String> result_amount = amount.showAndWait();
            result_amount.ifPresent(s -> serviceRepository.addServiceToReservation(selected.getId(),
                    Integer.parseInt(result.get()),
                    Integer.parseInt(s)));
        }

        handleListReservations();
    }

    @FXML
    public void handleIssueInvoice() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");


        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        if (selected.getState() != Reservation.State.CHECKED_OUT) {
            alert.setContentText("Reservation must be in CHECK_OUT state to issue invoice.");
            alert.showAndWait();
            return;
        }

        if (invoiceRepository.findByReservationId(selected.getId()).isPresent()) {
            alert.setContentText("There is already a invoice issued");
            alert.showAndWait();
            return;
        }

        var invoice = invoiceService.calculateInvoice(selected.getId());
        invoiceRepository.save(invoice);

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Invoice Issued");
        info.setHeaderText("Invoice for Reservation #" + selected.getId());
        info.setContentText(
                "Total Rooms: " + String.format("%.2f", invoice.getTotalRooms()) + "€\n" +
                        "Total Services: " + String.format("%.2f", invoice.getTotalServices()) + "€\n" +
                        "Discount: " + String.format("%.2f", invoice.getDiscount()) + "€\n" +
                        "Total Final: " + String.format("%.2f", invoice.getTotalFinal()) + "€"
        );
        info.showAndWait();

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

    @FXML
    public void handleViewInvoice() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText("No reservation selected");

        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.showAndWait();
            return;
        }

        var invoiceOptional = invoiceRepository.findByReservationId(selected.getId());
        if (invoiceOptional.isPresent()) {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            var invoice = invoiceOptional.get();
            alert1.setTitle("Invoice");
            alert1.setContentText(
                    "Invoice of Reservation #" + selected.getId() + "\n" +
                    "Total Rooms: " + String.format("%.2f", invoice.getTotalRooms()) + "€\n" +
                            "Total Services: " + String.format("%.2f", invoice.getTotalServices()) + "€\n" +
                            "Discount: " + String.format("%.2f", invoice.getDiscount()) + "€\n" +
                            "Total Final: " + String.format("%.2f", invoice.getTotalFinal()) + "€"
            );
            alert1.showAndWait();
        }else {
            alert.setTitle("Warning");
            alert.setContentText("Invoice doesn't exist");
            alert.showAndWait();
        }
    }

}
