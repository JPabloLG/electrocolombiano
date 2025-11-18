package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.InstallmentDTO;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstallmentDAOImpl implements InstallmentDAO {

    private final Connection connection;

    public InstallmentDAOImpl() {
        this.connection = JDBC.getConnection();
    }

    public InstallmentDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(InstallmentDTO installment) {
        String sql = "INSERT INTO Installment (installmentId, installmentCount, installmentValue, " +
                "installmentDate, paymentId) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, installment.getId());
            pstmt.setInt(2, installment.getInstallmentCount());
            pstmt.setBigDecimal(3, installment.getInstallmentValue());
            pstmt.setDate(4, Date.valueOf(installment.getInstallmentDate()));
            pstmt.setString(5, installment.getPaymentId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al crear la cuota");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la cuota: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(InstallmentDTO installment) {
        String sql = "UPDATE installments SET installment_count = ?, installment_value = ?, " +
                "installment_date = ?, payment_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, installment.getInstallmentCount());
            pstmt.setBigDecimal(2, installment.getInstallmentValue());
            pstmt.setDate(3, Date.valueOf(installment.getInstallmentDate()));
            pstmt.setString(4, installment.getPaymentId());
            pstmt.setString(5, installment.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Cuota no encontrada con ID: " + installment.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la cuota: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM Installment WHERE iinstallmentId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Cuota no encontrada con ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la cuota: " + e.getMessage(), e);
        }
    }

    @Override
    public InstallmentDTO findById(String id) {
        String sql = "SELECT iinstallmentId, installmentCount, installmentValue, installmentDate, paymentId " +
                "FROM Installment WHERE installmentId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstallmentDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la cuota por ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<InstallmentDTO> getAll() {
        List<InstallmentDTO> installments = new ArrayList<>();
        String sql = "SELECT id, installment_count, installment_value, installment_date, payment_id " +
                "FROM installments ORDER BY installment_count";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                installments.add(mapResultSetToInstallmentDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las cuotas: " + e.getMessage(), e);
        }

        return installments;
    }

    @Override
    public List<InstallmentDTO> findByPaymentId(String paymentId) {
        List<InstallmentDTO> installments = new ArrayList<>();
        String sql = "SELECT installmentId, installmentCount, installmentValue, installmentDate, paymentId " +
                "FROM Installment WHERE paymentId = ? ORDER BY installmentCount";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    installments.add(mapResultSetToInstallmentDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cuotas por pago: " + e.getMessage(), e);
        }

        return installments;
    }

    /**
     * Eliminar todas las cuotas de un pago
     */
    public void deleteByPaymentId(String paymentId) {
        String sql = "DELETE FROM Installment WHERE paymentId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, paymentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cuotas del pago: " + e.getMessage(), e);
        }
    }

    /**
     * Mapear ResultSet a InstallmentDTO
     */
    private InstallmentDTO mapResultSetToInstallmentDTO(ResultSet rs) throws SQLException {
        return InstallmentDTO.builder()
                .id(rs.getString("installmentId"))
                .installmentCount(rs.getInt("installmentCount"))
                .installmentValue(rs.getBigDecimal("installmentValue"))
                .installmentDate(rs.getDate("installmentDate").toLocalDate())
                .paymentId(rs.getString("paymentId"))
                .build();
    }

    /**
     * Obtener cuotas pendientes de pago (si agregas un campo de estado)
     */
    public List<InstallmentDTO> getPendingInstallments(String paymentId) {
        List<InstallmentDTO> installments = new ArrayList<>();
        String sql = "SELECT installmentId, installmentCount, installmentValue, installmentDate, paymentId " +
                "FROM Installment " +
                "WHERE paymentId = ? AND installmentDate >= CURDATE() " +
                "ORDER BY installmentCount";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    installments.add(mapResultSetToInstallmentDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener cuotas pendientes: " + e.getMessage(), e);
        }

        return installments;
    }
}
