package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.*;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDAOImpl implements SaleDAO {

    private final Connection connection;
    private final ProductDAO productDAO;
    private final PaymentDAO paymentDAO;
    private final UserDAO userDAO;

    public SaleDAOImpl() {
        this.connection = JDBC.getConnection();
        this.productDAO = new ProductDAOImpl(connection);
        this.paymentDAO = new PaymentDAOImpl(connection);
        this.userDAO = new UserDAOImpl(connection);
    }

    public SaleDAOImpl(Connection connection) {
        this.connection = connection;
        this.productDAO = new ProductDAOImpl(connection);
        this.paymentDAO = new PaymentDAOImpl(connection);
        this.userDAO = new UserDAOImpl(connection);
    }

    @Override
    public void save(SaleDTO sale) {
        String sql = "INSERT INTO Sale (id, saleDate, employedId, userId, subtotal, totalPrice) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

                System.out.println("=== DATOS DE LA VENTA ===");
                System.out.println("ID: " + sale.getId());
                System.out.println("Fecha: " + sale.getDateSale());
                System.out.println("Empleado: " + (sale.getEmployee() != null ? sale.getEmployee().getCedula() : "NULL"));
                System.out.println("Cliente: " + sale.getCustomerId());
                System.out.println("Subtotal: " + sale.getSubtotal());
                System.out.println("TotalPrice: " + sale.getTotalPrice());
                System.out.println("Productos: " + (sale.getProducts() != null ? sale.getProducts().size() : 0));
                System.out.println("=========================");

                pstmt.setString(1, sale.getId());
                pstmt.setDate(2, Date.valueOf(sale.getDateSale()));
                pstmt.setString(3, sale.getEmployee().getCedula());
                pstmt.setString(4, sale.getCustomerId());
                pstmt.setBigDecimal(5, sale.getSubtotal());
                pstmt.setBigDecimal(6, sale.getTotalPrice());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Error al crear la venta, no se insertaron filas.");
                }

                // Guardar productos de la venta
                saveSaleProducts(sale.getId(), sale.getProducts());

                // Guardar pagos si existen
                if (sale.getPayments() != null && !sale.getPayments().isEmpty()) {
                    for (PaymentDTO payment : sale.getPayments()) {
                        paymentDAO.save(payment);
                    }
                }

                // ✅ CORRECCIÓN: Usar quantity en lugar de stock
                for (ProductDTO product : sale.getProducts()) {
                    System.out.println("Actualizando stock de: " + product.getName() +
                            " | Stock actual: " + product.getStock() +
                            " | Cantidad a restar: " + product.getQuantity());
                    updateProductStock(product.getId(), product.getQuantity());
                }

                connection.commit();
                System.out.println("✓ Venta guardada exitosamente");

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("✗ Error en transacción, realizando rollback");
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la venta: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error al restaurar autocommit: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Guarda los productos asociados a una venta en la tabla intermedia
     */
    private void saveSaleProducts(String saleId, List<ProductDTO> products) throws SQLException {
        String sql = "INSERT INTO Sale_Product (saleId, productId, quantity) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (ProductDTO product : products) {
                int quantity = product.getQuantity();
                pstmt.setString(1, saleId);
                pstmt.setString(2, product.getId());
                pstmt.setInt(3, quantity);

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("✓ Productos guardados en Sale_Product: " + products.size());
        }
    }

    /**
     * Actualiza el stock de un producto después de una venta
     */
    private void updateProductStock(String productId, int quantitySold) throws SQLException {
        String sql = "UPDATE Product SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantitySold);
            pstmt.setString(2, productId);
            pstmt.setInt(3, quantitySold);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Stock insuficiente para el producto: " + productId);
            }
        }
    }

    @Override
    public void update(SaleDTO sale) {
        String sql = "UPDATE Sale SET dateSale = ?, employedId = ?, customerId = ?, " +
                "subtotal = ?, totalPrice = ? WHERE id = ?";

        try {
            connection.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(sale.getDateSale()));
                pstmt.setString(2, sale.getEmployee().getCedula());
                pstmt.setString(3, sale.getCustomerId());
                pstmt.setBigDecimal(4, sale.getSubtotal());
                pstmt.setBigDecimal(5, sale.getTotalPrice());
                pstmt.setString(6, sale.getId());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Error al actualizar la venta, venta no encontrada con ID: " + sale.getId());
                }

                // Actualizar productos (eliminar y reinsertar)
                deleteSaleProducts(sale.getId());
                saveSaleProducts(sale.getId(), sale.getProducts());

                connection.commit(); // Confirmar transacción

            } catch (SQLException e) {
                connection.rollback(); // Revertir cambios
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la venta: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error al restaurar autocommit: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Elimina los productos asociados a una venta
     */
    private void deleteSaleProducts(String saleId) throws SQLException {
        String sql = "DELETE FROM SaleProduct WHERE saleId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) {
        try {
            connection.setAutoCommit(false); // Iniciar transacción

            try {
                // Eliminar productos de la venta
                deleteSaleProducts(id);

                // Eliminar pagos de la venta
                deletePaymentsBySaleId(id);

                // Eliminar la venta
                String sql = "DELETE FROM Sale WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, id);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected == 0) {
                        throw new SQLException("Error al eliminar la venta, venta no encontrada con ID: " + id);
                    }
                }

                connection.commit(); // Confirmar transacción

            } catch (SQLException e) {
                connection.rollback(); // Revertir cambios
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la venta: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error al restaurar autocommit: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Elimina los pagos asociados a una venta
     */
    private void deletePaymentsBySaleId(String saleId) throws SQLException {
        String sql = "DELETE FROM Payment WHERE saleId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public SaleDTO findById(String id) {
        String sql = "SELECT s.id, s.dateSale, s.employedId, s.customerId, s.subtotal, s.totalPrice " +
                "FROM Sale s WHERE s.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    SaleDTO sale = mapResultSetToSaleDTO(rs);

                    // Cargar productos de la venta
                    sale.setProducts(findProductsBySaleId(id));

                    // Cargar pagos de la venta
                    sale.setPayments(paymentDAO.findBySaleId(id));

                    return sale;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la venta por ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Obtiene los productos asociados a una venta
     */
    private List<ProductDTO> findProductsBySaleId(String saleId) throws SQLException {
        String sql = "SELECT p.id, p.unitPrice, p.purchaseValue, p.stock, p.name, " +
                "sp.quantity, sp.unitPrice as salePrice, " +
                "pc.id as categoryId, pc.categoryName, pc.iva, pc.profitMargin " +
                "FROM Product p " +
                "INNER JOIN SaleProduct sp ON p.id = sp.productId " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "WHERE sp.saleId = ?";

        List<ProductDTO> products = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProductCategoryDTO category = ProductCategoryDTO.builder()
                            .id(rs.getInt("categoryId"))
                            .categoryName(rs.getString("categoryName"))
                            .iva(rs.getBigDecimal("iva"))
                            .profitMargin(rs.getBigDecimal("profitMargin"))
                            .build();

                    ProductDTO product = ProductDTO.builder()
                            .id(rs.getString("id"))
                            .name(rs.getString("name"))
                            .unitPrice(rs.getBigDecimal("unitPrice"))
                            .purchaseValue(rs.getBigDecimal("purchaseValue"))
                            .stock(rs.getInt("stock"))
                            //revisarrrrrrrrrrrrrrrr
                            .stock(rs.getInt("quantity"))
                            .category(category)
                            .build();

                    products.add(product);
                }
            }
        }

        return products;
    }

    @Override
    public List<SaleDTO> getAll() {
        List<SaleDTO> sales = new ArrayList<>();
        String sql = "SELECT s.id, s.dateSale, s.employedId, s.customerId, s.subtotal, s.totalPrice " +
                "FROM Sale s ORDER BY s.dateSale DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SaleDTO sale = mapResultSetToSaleDTO(rs);
                // Por optimización, no cargamos productos y pagos en getAll
                // Se pueden cargar bajo demanda con findById
                sales.add(sale);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las ventas: " + e.getMessage(), e);
        }

        return sales;
    }

    @Override
    public boolean exists(String saleId) {
        String sql = "SELECT COUNT(*) FROM Sale WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, saleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de la venta: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Mapea un ResultSet a un objeto SaleDTO
     */
    private SaleDTO mapResultSetToSaleDTO(ResultSet rs) throws SQLException {
        String employeeId = rs.getString("employeeId");
        UserDTO employee = userDAO.findByCedula(employeeId);

        if (employee == null) {
            throw new SQLException("Empleado no encontrado con ID: " + employeeId);
        }

        return SaleDTO.builder()
                .id(rs.getString("id"))
                .dateSale(rs.getDate("dateSale").toLocalDate())
                .employee(employee)
                .customerId(rs.getString("customerId"))
                .subtotal(rs.getBigDecimal("subtotal"))
                .totalPrice(rs.getBigDecimal("totalPrice"))
                .build();
    }

    // ==================== CONSULTAS ESPECÍFICAS DEL PROYECTO ====================

    @Override
    public List<SaleDTO> findByCustomerId(String customerId) {
        List<SaleDTO> sales = new ArrayList<>();
        String sql = "SELECT s.id, s.dateSale, s.employedId, s.customerId, s.subtotal, s.totalPrice " +
                "FROM Sale s WHERE s.customerId = ? ORDER BY s.dateSale DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapResultSetToSaleDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por cliente: " + e.getMessage(), e);
        }

        return sales;
    }

    @Override
    public List<SaleDTO> findByEmployeeId(String employeeId) {
        List<SaleDTO> sales = new ArrayList<>();
        String sql = "SELECT s.id, s.dateSale, s.employedId, s.customerId, s.subtotal, s.totalPrice " +
                "FROM Sale s WHERE s.employedId = ? ORDER BY s.dateSale DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapResultSetToSaleDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por empleado: " + e.getMessage(), e);
        }

        return sales;
    }

    @Override
    public List<SaleDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SaleDTO> sales = new ArrayList<>();
        String sql = "SELECT s.id, s.dateSale, s.employedId, s.customerId, s.subtotal, s.totalPrice " +
                "FROM Sale s WHERE s.dateSale BETWEEN ? AND ? ORDER BY s.dateSale DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(mapResultSetToSaleDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas por rango de fechas: " + e.getMessage(), e);
        }

        return sales;
    }

    @Override
    public BigDecimal getTotalSalesByMonth(int year, int month) {
        String sql = "SELECT SUM(totalPrice) as total FROM Sale " +
                "WHERE YEAR(saleDate) = ? AND MONTH(saleDate) = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener total de ventas por mes: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public int countSalesByPaymentType(LocalDate startDate, LocalDate endDate, String paymentType) {
        String sql = "SELECT COUNT(DISTINCT s.id) as count " +
                "FROM Sale s " +
                "INNER JOIN Payment p ON s.id = p.saleId " +
                "WHERE s.dateSale BETWEEN ? AND ? AND p.paymentType = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            pstmt.setString(3, paymentType);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar ventas por tipo de pago: " + e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public BigDecimal getTotalIVAByQuarter(int year, int quarter) {
        // Validar quarter (1-4)
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("El trimestre debe estar entre 1 y 4. Recibido: " + quarter);
        }

        // Calcular meses del trimestre
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;

        // ✅ CAMBIO: Usar p.unitPrice en lugar de sp.unitPrice
        String sql = "SELECT ISNULL(SUM(sp.quantity * p.unitPrice * pc.iva), 0) as totalIVA " +
                "FROM Sale s " +
                "INNER JOIN Sale_Product sp ON s.id = sp.saleId " +
                "INNER JOIN Product p ON sp.productId = p.id " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "WHERE YEAR(s.saleDate) = ? AND MONTH(s.saleDate) BETWEEN ? AND ?";

        System.out.println("=== CALCULANDO IVA POR TRIMESTRE ===");
        System.out.println("Año: " + year);
        System.out.println("Trimestre: " + quarter + " (meses " + startMonth + "-" + endMonth + ")");

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, startMonth);
            pstmt.setInt(3, endMonth);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);  // Usar índice en lugar de alias
                    BigDecimal resultado = total != null ? total : BigDecimal.ZERO;

                    System.out.println("✓ Total IVA del trimestre: $" + String.format("%,.2f", resultado));
                    return resultado;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener IVA por trimestre: " + e.getMessage());
            throw new RuntimeException("Error al obtener IVA por trimestre: " + e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Método adicional: Obtener ventas con detalles completos para reportes
     */
    public List<SaleDTO> getAllWithDetails() {
        List<SaleDTO> sales = getAll();

        for (SaleDTO sale : sales) {
            try {
                sale.setProducts(findProductsBySaleId(sale.getId()));
                sale.setPayments(paymentDAO.findBySaleId(sale.getId()));
            } catch (SQLException e) {
                throw new RuntimeException("Error al cargar detalles de la venta: " + e.getMessage(), e);
            }
        }

        return sales;
    }

    /**
     * Método adicional: Verificar si un cliente tiene ventas a crédito pendientes
     */
    public boolean hasActiveCreditSales(String customerId) {
        String sql = "SELECT COUNT(*) FROM Sale s " +
                "INNER JOIN Payment p ON s.id = p.saleId " +
                "WHERE s.customerId = ? AND p.paymentType = 'CREDIT' AND p.pendingAmount > 0";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar créditos activos: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Cuenta el total de ventas realizadas en un mes específico
     * @param year Año (ej: 2025)
     * @param month Mes (1-12)
     * @return Número de ventas en ese mes
     */
    @Override
    public int countSalesByMonth(int year, int month) {
        // Validar parámetros
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        String sql = "SELECT COUNT(*) FROM Sale WHERE YEAR(saleDate) = ? AND MONTH(saleDate) = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            System.out.println("=== EJECUTANDO CONSULTA ===");
            System.out.println("SQL: " + sql);
            System.out.println("Año: " + year + " | Mes: " + month);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("✓ Resultado: " + count + " ventas");
                    return count;
                } else {
                    System.out.println("⚠️  ResultSet vacío");
                    return 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error en SQL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al contar ventas por mes: " + e.getMessage(), e);
        }
    }
}