package com.marruky.repository;

import com.marruky.db.DataBaseConnection;
import com.marruky.exception.DataBaseException;
import com.marruky.exception.NotFoundException;
import com.marruky.model.User;
import com.marruky.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRepository {

    public User findByUsername(String username) {
        try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getBoolean("active"), rs.getTimestamp("created_at").toLocalDateTime());

            }
            throw new NotFoundException("Username doesn't exist");

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }


    public int save(User user) {
        try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, active) VALUES(?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtil.hash(user.getPassword()));
            stmt.setBoolean(3, user.isActive());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    public void delete(String username) {
        try (Connection conn = DataBaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }
}
