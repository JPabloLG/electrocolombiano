package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.Rol;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;

public class UserDAOImpl implements UserDAO{

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(UserDTO user) {
        /*String sql = "INSERT INTO users (name, email, identifier, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getId());
            stmt.setString(4, user.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public UserDTO findById(int id) {
        /*String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserDTO(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("user_id"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    public UserDTO findByEmail(String email) {
        /*String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserDTO(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("user_id"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    public UserDTO findByUserName(String username) {

        String sql = "SELECT u.cedula, u.userName, u.fullName, u.password, " +
                "r.id, r.roleName " +
                "FROM SystemUser u " +
                "JOIN rol r ON u.role_id = r.id " +
                "WHERE u.userName = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    Rol rol = new Rol(
                            rs.getString("roleName"),
                            rs.getInt("id")
                    );

                    return new UserDTO(
                            rs.getString("cedula"),
                            rs.getString("fullName"),
                            rs.getString("userName"),
                            rs.getString("password"),
                            rol
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    @Override
    public LinkedList<UserDTO> findAll() {
        LinkedList<UserDTO> users = new LinkedList<>();
        /*LinkedList<UserDTO> students = new LinkedList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UserDTO student = new UserDTO(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("user_id"),
                        rs.getString("password")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;*/
        return users;
    }

    @Override
    public void update(UserDTO user) {
        /*String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void delete(int id) {

    }
}
