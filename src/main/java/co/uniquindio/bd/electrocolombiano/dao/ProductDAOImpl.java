package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;
import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    private final Connection connection;

    public ProductDAOImpl() {
        this.connection = JDBC.getConnection();
    }

    public ProductDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(ProductDTO product) {
        String sql = "INSERT INTO Product (id, unitPrice, purchaseValue, stock, name ,categoryId) " +
                "VALUES (?, ?, ?, ?, ?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getId());
            pstmt.setBigDecimal(2, product.getUnitPrice());
            pstmt.setBigDecimal(3, product.getPurchaseValue());
            pstmt.setInt(4, product.getStock());
            pstmt.setString(5, product.getName());
            pstmt.setInt(6, product.getCategory().getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al crear el producto, no se insertaron filas.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ProductDTO product) {
        String sql = "UPDATE Product SET unitPrice = ?, purchaseValue = ?, " +
                "stock = ?, categoryId = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, product.getUnitPrice());
            pstmt.setBigDecimal(2, product.getPurchaseValue());
            pstmt.setInt(3, product.getStock());
            pstmt.setInt(4, product.getCategory().getId());
            pstmt.setString(5, product.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al actualizar el producto, producto no encontrado con ID: " + product.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Product WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al eliminar el producto, producto no encontrado con ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductDTO findByName(String name) {
        String sql = "SELECT p.id, p.unitPrice, p.purchaseValue, p.stock, p.name, " +
                "pc.id as categoryId, pc.categoryName, pc.iva, pc.profitMargin " +
                "FROM Product p " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "WHERE p.name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProductDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el producto por nombre: " + e.getMessage(), e); // Cambié el mensaje
        }

        return null;
    }

    @Override
    public List<ProductDTO> getAll() {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT p.id, p.unitPrice, p.purchaseValue, p.stock, " +
                "pc.id as categoryId, pc.categoryName, pc.iva, pc.profitMargin " +
                "FROM Product p " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "ORDER BY p.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProductDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los productos: " + e.getMessage(), e);
        }

        return products;
    }


    private ProductDTO mapResultSetToProductDTO(ResultSet rs) throws SQLException {
        // Crear CategoryDTO
        ProductCategoryDTO categoryDTO = ProductCategoryDTO.builder()
                .id(rs.getInt("categoryId"))
                .categoryName(rs.getString("categoryName"))
                .iva(rs.getBigDecimal("iva"))
                .profitMargin(rs.getBigDecimal("profitMargin"))
                .build();

        // Crear ProductDTO
        return ProductDTO.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .unitPrice(rs.getBigDecimal("unitPrice"))
                .purchaseValue(rs.getBigDecimal("purchaseValue"))
                .stock(rs.getInt("stock"))
                .category(categoryDTO)
                .build();
    }

    /**
     * Método adicional: Buscar productos por categoría
     */
    public List<ProductDTO> findByCategory(int categoryId) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT p.id, p.unitPrice, p.purchaseValue, p.stock, " +
                "pc.id as categoryId, pc.categoryName, pc.iva, pc.profitMargin " +
                "FROM Product p " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "WHERE p.categoryId = ? " +
                "ORDER BY p.id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProductDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar productos por categoría: " + e.getMessage(), e);
        }

        return products;
    }

    /**
     * Método adicional: Actualizar stock del producto
     */
    public void updateStock(String productId, int newStock) {
        String sql = "UPDATE Product SET stock = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setString(2, productId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al actualizar stock, producto no encontrado con ID: " + productId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el stock del producto: " + e.getMessage(), e);
        }
    }

    /**
     * Método adicional: Decrementar stock (útil para ventas)
     */
    public void decrementStock(String productId, int quantity) {
        String sql = "UPDATE Product SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, productId);
            pstmt.setInt(3, quantity);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error: Stock insuficiente o producto no encontrado con ID: " + productId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al decrementar el stock del producto: " + e.getMessage(), e);
        }
    }

    /**
     * Método adicional: Verificar si existe un producto
     */
    public boolean exists(String productId) {
        String sql = "SELECT COUNT(*) FROM Product WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia del producto: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Método adicional: Obtener productos con stock bajo
     */
    public List<ProductDTO> getLowStockProducts(int threshold) {
        List<ProductDTO> products = new ArrayList<>();
        String sql = "SELECT p.id, p.unitPrice, p.purchaseValue, p.stock, " +
                "pc.id as categoryId, pc.categoryName, pc.iva, pc.profitMargin " +
                "FROM Product p " +
                "INNER JOIN ProductCategory pc ON p.categoryId = pc.id " +
                "WHERE p.stock <= ? " +
                "ORDER BY p.stock ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, threshold);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProductDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage(), e);
        }

        return products;
    }
}
