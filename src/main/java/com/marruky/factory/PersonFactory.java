package com.marruky.factory;

import com.marruky.model.person.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonFactory {

    public static Client createClient(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String contact = rs.getString("contact");
        String nif = rs.getString("nif");
        Person.Type type = Person.Type.valueOf(rs.getString("type"));
        int loyaltyPoints = rs.getInt("loyalty_points");
        String enterprise = rs.getString("enterprise");

        return switch (type) {
            case CLIENT -> new Client(id, name, email, contact, nif, type, loyaltyPoints, enterprise);
            case VIP_CLIENT -> new VipClient(id, name, email, contact, nif, type, loyaltyPoints, enterprise);
            case ENTERPRISE_CLIENT -> new EnterpriseClient(id, name, email, contact, nif, type, loyaltyPoints, enterprise);
            default -> throw new IllegalArgumentException("Invalid type of client: " + type);
        };
    }

    public static Staff createStaff(ResultSet rs) throws SQLException{

        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String contact = rs.getString("contact");
        String nif = rs.getString("nif");
        Person.Type type = Person.Type.valueOf(rs.getString("type"));
        String position = rs.getString("position");
        double salary = rs.getDouble("salary");
        String shift = rs.getString("shift");

        return switch (type){
            case RECEPTIONIST -> new Receptionist(id, name, email, contact, nif, type, position, salary, shift);
            case MANAGER -> new Manager(id, name, email, contact, nif, type, position, salary, shift);
            default -> throw new IllegalArgumentException("Invalid type of staff: " + type);
        };
    }


}
