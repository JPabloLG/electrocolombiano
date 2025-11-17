package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDAOImpl implements ProductCategoryDAO {

    private final Connection connection;

    public ProductCategoryDAOImpl() {
        this.connection = JDBC.getConnection();
    }

    // Constructor alternativo para testing o inyección de dependencias
    public ProductCategoryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(ProductCategoryDTO productCategory) {
        String sql = "INSERT INTO ProductCategory (CategoryName, iva, profitMargin) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, productCategory.getCategoryName());
            pstmt.setBigDecimal(2, productCategory.getIva());
            pstmt.setBigDecimal(3, productCategory.getProfitMargin());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al crear la categoría, no se insertaron filas.");
            }

            // Opcional: obtener el ID generado automáticamente
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Categoría creada con ID: " + generatedId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la categoría de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(ProductCategoryDTO productCategory) {
        String sql = "UPDATE ProductCategory SET categoryName = ?, iva = ?, profitMargin = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCategory.getCategoryName());
            pstmt.setBigDecimal(2, productCategory.getIva());
            pstmt.setBigDecimal(3, productCategory.getProfitMargin());
            pstmt.setInt(4, productCategory.getId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al actualizar la categoría, categoría no encontrada con ID: " + productCategory.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la categoría de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        // Primero verificar si hay productos asociados a esta categoría
        if (hasAssociatedProducts(id)) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene productos asociados");
        }

        String sql = "DELETE FROM ProductCategory WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Error al eliminar la categoría, categoría no encontrada con ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la categoría de producto: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductCategoryDTO findById(int id) {
        String sql = "SELECT id, categoryName, iva, profitMargin FROM ProductCategory WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProductCategoryDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la categoría por ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<ProductCategoryDTO> getAll() {
        List<ProductCategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT id, categoryName, iva, profitMargin FROM ProductCategory ORDER BY categoryName";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToProductCategoryDTO(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todas las categorías: " + e.getMessage(), e);
        }

        return categories;
    }

    /**
     * Método auxiliar para mapear ResultSet a ProductCategoryDTO
     */
    private ProductCategoryDTO mapResultSetToProductCategoryDTO(ResultSet rs) throws SQLException {
        return ProductCategoryDTO.builder()
                .id(rs.getInt("id"))
                .categoryName(rs.getString("categoryName"))
                .iva(rs.getBigDecimal("iva"))
                .profitMargin(rs.getBigDecimal("profitMargin"))
                .build();
    }

    /**
     * Método adicional: Verificar si una categoría tiene productos asociados
     */
    private boolean hasAssociatedProducts(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Product WHERE categoryId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar productos asociados: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Método adicional: Buscar categoría por nombre
     */
    public ProductCategoryDTO findByName(String categoryName) {
        String sql = "SELECT id, categoryName, iva, profitMargin FROM ProductCategory WHERE categoryName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProductCategoryDTO(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la categoría por nombre: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Método adicional: Verificar si existe una categoría
     */
    public boolean exists(int categoryId) {
        String sql = "SELECT COUNT(*) FROM ProductCategory WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia de la categoría: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Método adicional: Verificar si existe una categoría por nombre (para evitar duplicados)
     */
    public boolean existsByName(String categoryName) {
        String sql = "SELECT COUNT(*) FROM ProductCategory WHERE categoryName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoryName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar existencia por nombre: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Método adicional: Obtener conteo de productos por categoría
     */
    public int getProductCount(int categoryId) {
        String sql = "SELECT COUNT(*) FROM Product WHERE categoryId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener conteo de productos: " + e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Método adicional: Obtener categorías con el IVA más alto
     */
    public List<ProductCategoryDTO> getCategoriesWithHighestIva(int limit) {
        List<ProductCategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT id, categoryName, iva, profitMargin FROM ProductCategory " +
                "ORDER BY iva DESC LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToProductCategoryDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener categorías con IVA más alto: " + e.getMessage(), e);
        }

        return categories;
    }

    /**
     * Método adicional: Obtener categorías con el margen de utilidad más alto
     */
    public List<ProductCategoryDTO> getCategoriesWithHighestProfitMargin(int limit) {
        List<ProductCategoryDTO> categories = new ArrayList<>();
        String sql = "SELECT id, categoryName, iva, profitMargin FROM ProductCategory " +
                "ORDER BY profitMargin DESC LIMIT ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToProductCategoryDTO(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener categorías con mayor margen: " + e.getMessage(), e);
        }

        return categories;
    }
}
