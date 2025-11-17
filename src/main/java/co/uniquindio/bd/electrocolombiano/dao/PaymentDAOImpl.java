package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.InstallmentDTO;
import co.uniquindio.bd.electrocolombiano.dto.PaymentDTO;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    private final Connection connection;
    private final InstallmentDAOImpl installmentDAO;

    public PaymentDAOImpl() {
        this.connection = JDBC.getConnection();
        this.installmentDAO = new InstallmentDAOImpl(connection);
    }

    public PaymentDAOImpl(Connection connection) {
        this.connection = connection;
        this.installmentDAO = new InstallmentDAOImpl(connection);
    }

    @Override
    public void save(PaymentDTO payment) {
        String sql = "INSERT INTO Payment (payMentId, totalPrice, isCredit) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, payment.getId());
            pstmt.setBigDecimal(2, payment.getTotalPrice());
            pstmt.setBoolean(3, payment.isCredit());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al crear el pago");
            }

            // Si es crédito, guardar las cuotas
            if (payment.isCredit() && payment.getInstallments() != null) {
                for (InstallmentDTO installment : payment.getInstallments()) {
                    installment.setPaymentId(payment.getId());
                    installmentDAO.save(installment);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el pago: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(PaymentDTO payment) {
        String sql = "UPDATE Payment SET totalPrice = ?, isCredit = ? WHERE payMentId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, payment.getTotalPrice());
            pstmt.setBoolean(2, payment.isCredit());
            pstmt.setString(3, payment.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Pago no encontrado con ID: " + payment.getId());
            }

            // Actualizar cuotas si es crédito
            if (payment.isCredit() && payment.getInstallments() != null) {
                // Eliminar cuotas existentes
                installmentDAO.deleteByPaymentId(payment.getId());

                // Insertar nuevas cuotas
                for (InstallmentDTO installment : payment.getInstallments()) {
                    installment.setPaymentId(payment.getId());
                    installmentDAO.save(installment);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el pago: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // Primero eliminar las cuotas asociadas
            installmentDAO.deleteByPaymentId(id);

            // Luego eliminar el pago
            String sql = "DELETE FROM Payment WHERE payMentId = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, id);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Pago no encontrado con ID: " + id);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el pago: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentDTO findById(String id) {
        String sql = "SELECT payMentId, totalPrice, isCredit FROM Payment WHERE payMentId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPaymentDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el pago por ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<PaymentDTO> getAll() {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT payMentId, totalPrice, isCredit FROM Payment ORDER BY payMentId";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los pagos: " + e.getMessage(), e);
        }

        return payments;
    }

    @Override
    public List<PaymentDTO> findBySaleId(String saleId) {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT p.payMentId, p.totalPrice, p.isCredit " +
                "FROM Payment p " +
                "INNER JOIN Sale_Product ON p.payMentId = sp.payMentId " +
                "WHERE sp.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPaymentDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar pagos por venta: " + e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Mapear ResultSet a PaymentDTO
     */
    private PaymentDTO mapResultSetToPaymentDTO(ResultSet rs) throws SQLException {
        String id = rs.getString("payMentId");
        boolean isCredit = rs.getBoolean("isCredit");

        PaymentDTO payment = PaymentDTO.builder()
                .id(id)
                .totalPrice(rs.getBigDecimal("totalPrice"))
                .isCredit(isCredit)
                .build();

        // Cargar cuotas si es crédito
        if (isCredit) {
            List<InstallmentDTO> installments = installmentDAO.findByPaymentId(id);
            payment.setInstallments(installments);
        }

        return payment;
    }

    /**
     * Obtener pagos a crédito
     */
    public List<PaymentDTO> getCreditPayments() {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT payMentId, totalPrice, isCredit FROM Payment WHERE isCredit = true";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener pagos a crédito: " + e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Obtener pagos de contado
     */
    @Override
    public List<PaymentDTO> getCashPayments() {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT payMentId, totalPrice, isCredit FROM Payment WHERE isCredit = false";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener pagos de contado: " + e.getMessage(), e);
        }

        return payments;
    }

    /**
     * Asociar pago a una venta
     */
    public void associateToSale(String paymentId, String saleId) {
        String sql = "INSERT INTO Sale (paymentId) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);
            pstmt.setString(2, paymentId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al asociar pago a venta: " + e.getMessage(), e);
        }
    }

    /**
     * Desasociar pago de una venta
     */
    public void disassociateFromSale(String paymentId, String saleId) {
        String sql = "DELETE FROM sale_payments WHERE sale_id = ? AND payment_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);
            pstmt.setString(2, paymentId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al desasociar pago de venta: " + e.getMessage(), e);
        }
    }
}
