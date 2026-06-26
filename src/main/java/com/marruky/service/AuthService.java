package com.marruky.service;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.AuthException;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.User;
import com.marruky.model.person.Person;
import com.marruky.model.person.Staff;
import com.marruky.repository.UserRepository;
import com.marruky.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username);
        if (!PasswordUtil.verify(password, user.getPassword())) {
            throw new AuthException("Password incorrect");
        }
        if (!user.isActive()) {
            throw new AuthException("User is not active");
        }
        return user;
    }


    public User registerClient(String username, String password, String name, String email,
                               String contact, String nif) {

        try {
            userRepository.findByUsername(username);
            throw new AuthException("This username already exists");
        } catch (NotFoundException ignored) {}

        Connection conn = DataBaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, active, created_at) VALUES(?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                String hashedPassword = PasswordUtil.hash(password);
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setBoolean(3, true);
                stmt.setDate(4, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                rs.next();
                int userId = rs.getInt(1);

                try (PreparedStatement statement = conn.prepareStatement("INSERT INTO people (user_id, name, email, contact, nif, type, created_at) " +
                        "VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, userId);
                    statement.setString(2, name);
                    statement.setString(3, email);
                    statement.setString(4, contact);
                    statement.setString(5, nif);
                    statement.setString(6, Person.Type.CLIENT.toString());
                    statement.setDate(7, Date.valueOf(LocalDate.now()));
                    statement.executeUpdate();
                    ResultSet resultSet = statement.getGeneratedKeys();
                    resultSet.next();

                    int peopleId = resultSet.getInt(1);

                    try (PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO clients (person_id, loyalty_points, enterprise) VALUES(?,?,?)")) {

                        stmt1.setInt(1, peopleId);
                        stmt1.setInt(2, 0);
                        stmt1.setString(3, null);
                        stmt1.executeUpdate();
                        conn.commit();

                        return new User(userId, username, password, true, LocalDateTime.now());


                    } catch (Exception e) {
                        throw new DataBaseException(e.getMessage(), e);
                    }

                } catch (SQLException e) {
                    throw new DataBaseException(e.getMessage(), e);
                }

            } catch (SQLException e) {
                conn.close();
                throw new DataBaseException(e.getMessage(), e);
            }

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DataBaseException(ex.getMessage(), ex);
            }
            throw new DataBaseException(e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new DataBaseException(ex.getMessage(), ex);
            }
        }
    }

    public void registerStaff(String username, String password,
                              String name, String email,
                              String contact, String nif,
                              String position, double salary,
                              String shift, String type) {

        try {
            userRepository.findByUsername(username);
            throw new AuthException("This username already exists");
        } catch (NotFoundException ignored) {
        }

        Connection conn = DataBaseConnection.getConnection();

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, active, created_at) VALUES(?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                String hashedPassword = PasswordUtil.hash(password);

                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setBoolean(3, true);
                stmt.setDate(4, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (!rs.next()) {
                    throw new DataBaseException("Couldn't register the staff");
                }

                int userId = rs.getInt(1);

                try (PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO people (user_id, name, email, contact, nif, type, created_at) " +
                        "VALUES (?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)){

                    stmt1.setInt(1, userId);
                    stmt1.setString(2, name);
                    stmt1.setString(3, email);
                    stmt1.setString(4, contact);
                    stmt1.setString(5, nif);
                    stmt1.setString(6, type);
                    stmt1.setDate(7, Date.valueOf(LocalDate.now()));
                    stmt1.executeUpdate();

                    ResultSet rs1 = stmt1.getGeneratedKeys();

                    if(!rs1.next()){
                        throw new DataBaseException("Couldn't register the staff");
                    }
                    int personId = rs1.getInt(1);

                    try(PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO staff(person_id, position, salary, shift) " +
                            "VALUES (?,?,?,?)")){

                        stmt2.setInt(1, personId);
                        stmt2.setString(2, position);
                        stmt2.setDouble(3, salary);
                        stmt2.setString(4, shift);
                        stmt2.executeUpdate();
                        conn.commit();

                    }catch (SQLException e){
                        throw new DataBaseException(e.getMessage(),e);
                    }


                }catch (SQLException e){
                    throw new DataBaseException(e.getMessage(), e);
                }

            } catch (SQLException e) {
                conn.close();
                throw new DataBaseException(e.getMessage(), e);
            }

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new DataBaseException(ex.getMessage(), ex);
            }
            throw new DataBaseException(e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new DataBaseException(ex.getMessage(), ex);
            }
        }
    }

}