package com.marruky.menu;

import com.marruky.exception.AuthException;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.Invoice;
import com.marruky.model.Reservation;
import com.marruky.model.User;
import com.marruky.model.person.Client;
import com.marruky.model.person.Person;
import com.marruky.model.room.Room;
import com.marruky.repository.*;
import com.marruky.service.AuthService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/*
1. Mostrar boas vindas
2. Pedir username e password
3. Chamar AuthService.verify()
4. Se sucesso → redireciona pelo role
5. Se falhar → mostra erro e volta ao início

 */

public class Menu {

    private RoomRepository roomRepository;
    private PersonRepository personRepository;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;
    private ServiceRepository serviceRepository;
    private InvoiceRepository invoiceRepository;


    public Menu(RoomRepository roomRepository, PersonRepository personRepository,
                ReservationRepository reservationRepository,
                UserRepository userRepository, ServiceRepository serviceRepository,
                InvoiceRepository invoiceRepository) {

        this.roomRepository = roomRepository;
        this.personRepository = personRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.invoiceRepository = invoiceRepository;

    }

    public void loginScreen() {
        AuthService auth = new AuthService(userRepository);
        Scanner scanner = new Scanner(System.in);
        String username, password;
        boolean notDone = true;
        User user = null;
        int option;

        do {
            System.out.println("==== WELCOME ====");
            System.out.println("1. LOGIN");
            System.out.println("2. REGISTER");
            System.out.println("3. Exit");
            System.out.print("Option: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option){
                case 1 -> {
                    System.out.print("Username: ");
                    username = scanner.nextLine();
                    System.out.print("Password: ");
                    password = scanner.nextLine();
                    try {
                        user = auth.login(username, password);
                        notDone = false;
                    } catch (NotFoundException e) {
                        System.out.println("Username or password wrong");
                    } catch (AuthException e) {
                        System.out.print(e.getMessage());
                    }
                }
                case 2 -> {
                    String name, email, contact, nif;

                    System.out.print("Name: ");
                    name = scanner.nextLine();

                    System.out.print("Email: ");
                    email = scanner.nextLine();

                    System.out.print("Contact: ");
                    contact = scanner.nextLine();

                    System.out.print("nif: ");
                    nif = scanner.nextLine();

                    System.out.print("Username: ");
                    username = scanner.nextLine();

                    System.out.print("Password: ");
                    password = scanner.nextLine();
                    try{
                        user = auth.register(username, password, name, email, contact, nif);
                        notDone = false;
                    }catch (AuthException e){
                        System.out.print(e.getMessage());
                    } catch (DataBaseException e) {
                        System.out.println("DB Error: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }

                }
                case 3 ->{
                    notDone = false;
                    break;
                }
            }

        } while (notDone);

        if (user == null){
            return;
        }
        Person person = new PersonRepository().findByUserId(user.getId());
        if (person.getType() == Person.Type.MANAGER || person.getType() == Person.Type.RECEPTIONIST) {
            loginStaff(person);
        } else {
            loginClient(person);
        }

    }

    public void loginStaff(Person person) {
        Scanner scanner = new Scanner(System.in);
        int option;
        do {
            System.out.println("==== STAFF MENU ====");
            System.out.println("1. List all reservations");
            System.out.println("2. Confirm the reservation");
            System.out.println("3. Check-in");
            System.out.println("4. Check-out");
            System.out.println("5. Add a service to reservation");
            System.out.println("6. Issue invoice");
            System.out.println("0. Logout");
            System.out.print("Option: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    List<Reservation> reservations = reservationRepository.findAll();
                    for (Reservation reservation : reservations) {
                        System.out.println(reservation.toString());
                    }
                }
                case 2 -> {
                    int clientId;
                    System.out.print("Client's ID: ");
                    clientId = scanner.nextInt();
                    scanner.nextLine();

                    List<Reservation> reservations = reservationRepository.findByClientId(clientId)
                            .stream()
                            .filter(r -> r.getState() == Reservation.State.PENDING)
                            .toList();
                    for (var reservation : reservations) {
                        System.out.println(reservation.toString());
                    }
                    int id;
                    System.out.print("Reservation's id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();

                    reservationRepository.updateState(id, Reservation.State.CONFIRMED);
                }
                case 3 -> {
                    int clientId;
                    System.out.print("Client's ID: ");
                    clientId = scanner.nextInt();
                    scanner.nextLine();

                    List<Reservation> reservations = reservationRepository.findByClientId(clientId)
                            .stream()
                            .filter(reservation -> Objects.equals(reservation.getCheckInDate(), LocalDate.now()))
                            .filter(r -> r.getState() == Reservation.State.CONFIRMED)
                            .toList();

                    for (var reservation : reservations) {
                        System.out.println(reservation.toString());
                    }
                    int id;
                    System.out.print("Reservation's id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();


                    var reservation = reservationRepository.findById(id);
                    reservationRepository.updateState(id, Reservation.State.CHECKED_IN);

                    var room = roomRepository.findById(reservation.getRoomId());
                    if (room.isPresent()) {
                        room.get().setAvailable(false);
                        roomRepository.update(room.get());
                    }
                }
                case 4 -> {
                    int clientId;
                    System.out.print("Client's ID: ");
                    clientId = scanner.nextInt();
                    scanner.nextLine();

                    List<Reservation> reservations = reservationRepository.findByClientId(clientId)
                            .stream()
                            .filter(r -> r.getState().equals(Reservation.State.CHECKED_IN))
                            .toList();

                    for (var reservation : reservations) {
                        System.out.println(reservation.toString());
                    }
                    int id;
                    System.out.print("Reservation's id: ");
                    id = scanner.nextInt();
                    scanner.nextLine();

                    var reservation = reservationRepository.findById(id);
                    reservationRepository.updateState(id, Reservation.State.CHECKED_OUT);

                    var room = roomRepository.findById(reservation.getRoomId());
                    if (room.isPresent()) {
                        room.get().setAvailable(true);
                        roomRepository.update(room.get());
                    }
                }
                case 5 -> {
                    int reservationId;
                    System.out.print("Reservation's ID: ");
                    reservationId = scanner.nextInt();
                    scanner.nextLine();

                    var reservation = reservationRepository.findById(reservationId);
                    if (reservation.getState() == Reservation.State.CHECKED_IN) {
                        for (var service : serviceRepository.findAll()) {
                            System.out.println(service.toString());
                        }
                        int service_id;
                        System.out.print("What's the service's ID: ");
                        service_id = scanner.nextInt();
                        scanner.nextLine();

                        int amount;
                        System.out.print("Amount: ");
                        amount = scanner.nextInt();
                        scanner.nextLine();

                        serviceRepository.addServiceToReservation(reservationId, service_id, amount);
                    } else {
                        System.out.println("Reservation is not checked in");
                    }
                }
                case 6 -> {
                    int reservationId;
                    double totalFinal;
                    double totalRooms = 0;
                    double totalServices = 0;
                    double discount;
                    System.out.print("Reservation's ID: ");
                    reservationId = scanner.nextInt();
                    scanner.nextLine();
                    Reservation reservation = reservationRepository.findById(reservationId);

                    if (invoiceRepository.findByReservationId(reservationId).isPresent()) {
                        System.out.println("Already issued invoice");
                        break;
                    }

                    var roomOptional = roomRepository.findById(reservation.getRoomId());
                    if (roomOptional.isPresent()) {
                        totalRooms = roomOptional.get().calculatePrice(reservation.getNumNights());
                    }

                    var reservationServices = serviceRepository.findByReservationId(reservationId);
                    for (var reservationService : reservationServices) {
                        totalServices += reservationService.getService().calculatePrice(reservationService.getAmount());
                    }

                    var client = personRepository.findById(reservation.getClientId());
                    double subtotal = totalRooms + totalServices;
                    discount = subtotal * client.calculateDiscount();
                    totalFinal = subtotal - discount;

                    int generatedI = invoiceRepository.save(new Invoice(0, reservationId, totalRooms, totalServices, discount, totalFinal, null));

                    System.out.println("Invoice issued successfully! ID: " + generatedI);
                    System.out.printf("Total Rooms: %.2f%n", totalRooms);
                    System.out.printf("Total Services: %.2f%n", totalServices);
                    System.out.printf("Discount: %.2f%n", discount);
                    System.out.printf("Total Final: %.2f%n", totalFinal);

                }
            }
        } while (option != 0);
    }

    public void loginClient(Person person) {
        Scanner scanner = new Scanner(System.in);
        int option;
        Client client = personRepository.findClientByPersonId(person.getId());
        do {
            System.out.println("==== CLIENT MENU ====");
            System.out.println("1. See available rooms");
            System.out.println("2. Make a reservation");
            System.out.println("3. See my reservations");
            System.out.println("0. Logout");
            System.out.print("Option: ");
            option = scanner.nextInt();
            scanner.nextLine();


            switch (option) {
                case 1 -> {
                    for (var roomAvailable : roomRepository.findAvailable()) {
                        System.out.println(roomAvailable.toString());
                    }
                }
                case 2 -> {
                    int roomId;
                    LocalDate checkInDate, checkOutDate;
                    for (var roomAvailable : roomRepository.findAvailable()) {
                        System.out.println(roomAvailable.toString());
                    }
                    System.out.print("Room's ID: ");
                    roomId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Check-In (yyyy-MM-dd): ");
                    checkInDate = Date.valueOf(scanner.nextLine()).toLocalDate();

                    System.out.print("Check-Out (yyyy-MM-dd): ");
                    checkOutDate = Date.valueOf(scanner.nextLine()).toLocalDate();

                    if (checkOutDate.isBefore(checkInDate)) {
                        System.out.println("Check-Out date must be after than Check-In date");
                        break;
                    }

                    var reservation = new Reservation(0, client.getId(),
                            roomId, 0, checkInDate, checkOutDate, 0,
                            Reservation.State.PENDING,
                            null, LocalDateTime.now());

                    reservationRepository.save(reservation);
                    System.out.println("Reservation created");

                }
                case 3 -> {
                    var reservations = reservationRepository.findByClientId(client.getId());
                    if (reservations.isEmpty()) {
                        System.out.println("No reservations found.");
                    } else {
                        for (var reservation : reservations) {
                            System.out.println(reservation.toString());
                        }
                    }
                }
            }
        } while (option != 0);
    }
}
