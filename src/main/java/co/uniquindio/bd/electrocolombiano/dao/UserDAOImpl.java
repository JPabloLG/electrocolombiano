package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.UserDTO;
import co.uniquindio.bd.electrocolombiano.model.Rol;
import co.uniquindio.bd.electrocolombiano.model.SystemUser;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class UserDAOImpl implements UserDAO{

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(UserDTO user) {
        String sql = "INSERT INTO SystemUser (userName, cedula, password, role_id, fullName) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getCedula());
            stmt.setString(3, user.getPassword());
            stmt.setString(5, user.getFullName());


            // Mapear nombre de rol a ID
            int roleId = mapearRolAId(user.getRole().getRoleName());
            stmt.setInt(4, roleId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para mapear nombre de rol a ID
    private int mapearRolAId(String roleName) {
        switch (roleName) {
            case "Administrador":
                return 1; // ID para Administrador
            case "Vendedor": //ID para Vendedor
                return 2;
            case "Cliente":
                return 3;// ID para Cliente
            default:
                return 2; // Por defecto Vendedor
        }
    }



    @Override
    public UserDTO findByCedula(String cedula) {
        System.out.println("=== Buscando usuario por cédula ===");
        System.out.println("Cédula buscada: '" + cedula + "'");

        String sql = "SELECT u.cedula, u.userName, u.fullName, u.password, " +
                "r.id, r.roleName " +
                "FROM SystemUser u " +
                "JOIN Rol r ON u.role_id = r.id " +
                "WHERE u.cedula = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cedula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Usuario encontrado por cédula: " + cedula);
                    System.out.println("   - Nombre: " + rs.getString("fullName"));
                    System.out.println("   - Usuario: " + rs.getString("userName"));
                    System.out.println("   - Rol: " + rs.getString("roleName"));

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
                } else {
                    System.out.println("❌ Usuario NO encontrado por cédula: " + cedula);
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error buscando por cédula: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public UserDTO findByUserName(String userName) {

        String sql = "SELECT u.cedula, u.userName, u.fullName, u.password, " +
                "r.id, r.roleName " +
                "FROM SystemUser u " +
                "JOIN Rol r ON u.role_id = r.id " +
                "WHERE u.userName = ?";

        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    Rol rol = new Rol(
                            rs.getString("roleName"),
                            rs.getInt("id")
                    );
                    System.out.println("Buscando usuario: '" + userName + "'");
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
        String sql = "SELECT * FROM SystemUser";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            Rol rol = new Rol(
                    rs.getString("roleName"),
                    rs.getInt("id")
            );
            while (rs.next()) {
                UserDTO student = new UserDTO(
                        rs.getString("cedula"),
                        rs.getString("fullName"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rol
                );
                users.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void update(UserDTO user) {
        String sql = "UPDATE SystemUser SET userName = ?, password = ?, fullName = ? WHERE cedula = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String cedula) {
        String sql = "DELETE FROM SysteUser WHERE cedula = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cedula);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al eliminar el usuario, usuario no encontrado con Cedula: " + cedula);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el usuario: " + e.getMessage(), e);
        }
    }
}
