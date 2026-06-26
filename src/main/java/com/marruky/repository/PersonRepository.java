package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.factory.PersonFactory;
import com.marruky.model.person.Client;
import com.marruky.model.person.Person;
import com.marruky.model.person.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonRepository {

    public List<Client> findAllClients() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people p INNER JOIN clients c ON c.person_id = p.id");
             ResultSet rs = stmt.executeQuery()) {

            List<Client> clients = new ArrayList<>();
            while (rs.next()) {
                clients.add(PersonFactory.createClient(rs));
            }
            return clients;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }


    public List<Staff> findAllStaff() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people p INNER JOIN staff s ON p.id = s.person_id");
             ResultSet rs = stmt.executeQuery()) {

            List<Staff> staff = new ArrayList<>();
            while (rs.next()) {
                staff.add(PersonFactory.createStaff(rs));
            }
            return staff;

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Person findById(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE id=?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Person.Type type = Person.Type.valueOf(rs.getString("type"));
                if (type == Person.Type.MANAGER || type == Person.Type.RECEPTIONIST) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT p.*, s.* FROM people p INNER JOIN staff s ON s.person_id = p.id WHERE p.id = ?")) {
                        stmt2.setInt(1, id);
                        ResultSet rs2 = stmt2.executeQuery();
                        rs2.next();
                        return PersonFactory.createStaff(rs2);
                    }
                } else {
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT p.*, c.* FROM people p INNER JOIN clients c ON c.person_id = p.id WHERE p.id = ?")) {
                        stmt2.setInt(1, id);
                        ResultSet rs2 = stmt2.executeQuery();
                        rs2.next();
                        return PersonFactory.createClient(rs2);
                    }
                }
            }
            throw new NotFoundException("There isn't this id in the system");
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Person findByUserId(int userId) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Person.Type type = Person.Type.valueOf(rs.getString("type"));
                if (type == Person.Type.MANAGER || type == Person.Type.RECEPTIONIST) {
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT p.*, s.* FROM people p INNER JOIN staff s ON s.person_id = p.id WHERE p.id = ?")) {
                        stmt2.setInt(1, rs.getInt("id"));
                        ResultSet rs2 = stmt2.executeQuery();
                        rs2.next();
                        return PersonFactory.createStaff(rs2);
                    }
                } else {
                    try (PreparedStatement stmt2 = conn.prepareStatement("SELECT p.*, c.* FROM people p INNER JOIN clients c ON c.person_id = p.id WHERE p.id = ?")) {
                        stmt2.setInt(1, rs.getInt("id"));
                        ResultSet rs2 = stmt2.executeQuery();
                        rs2.next();
                        return PersonFactory.createClient(rs2);
                    }
                }
            }
            throw new NotFoundException("User not found");
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int saveClient(Client client) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO clients (person_id, loyalty_points, enterprise) VALUES (?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, client.getId());
            stmt.setInt(2, client.getLoyaltyPoints());
            stmt.setString(3, client.getEnterprise());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public int saveStaff(Staff employee) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO staff (person_id, position, salary, shift) VALUES(?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, employee.getId());
            stmt.setString(2, employee.getPosition());
            stmt.setDouble(3, employee.getSalary());
            stmt.setString(4, employee.getShift());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }


    public void update(Person person) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE people SET name = ?, email = ?, contact = ?, nif = ?, type = ? WHERE id = ?")) {

            stmt.setString(1, person.getName());
            stmt.setString(2, person.getEmail());
            stmt.setString(3, person.getContact());
            stmt.setString(4, person.getNif());
            stmt.setString(5, person.getType().toString());
            stmt.setInt(6, person.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE from people WHERE id=?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Client findClientByPersonId(int personId) {
        try (Connection conn = DataBaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT p.*, c.id AS client_id, c.loyalty_points, c.enterprise FROM people p " +
                "INNER JOIN clients c ON c.person_id = p.id " +
                "WHERE c.person_id = ?")) {

            stmt.setInt(1, personId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return new Client(rs.getInt("client_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("contact"),
                        rs.getString("nif"), Person.Type.valueOf(rs.getString("type")),
                        rs.getInt("loyalty_points"), rs.getString("enterprise"));
            }
            throw new NotFoundException("Client wasn't found");

        }catch (SQLException e){
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public Client findClientById(int clientId) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.*, c.id AS client_id, c.loyalty_points, c.enterprise " +
                             "FROM people p " +
                             "INNER JOIN clients c ON c.person_id = p.id " +
                             "WHERE c.id = ?")) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("client_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("contact"),
                        rs.getString("nif"),
                        Person.Type.valueOf(rs.getString("type")),
                        rs.getInt("loyalty_points"),
                        rs.getString("enterprise")
                );
            }
            throw new NotFoundException("Client not found");

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

}
