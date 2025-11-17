package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;

import java.util.List;

public interface ProductDAO {

    void save(ProductDTO product);
    void update(ProductDTO product);
    void delete(int id);
    ProductDTO findByName(String nameProduct);
    List<ProductDTO> getAll();
    boolean exists(String productId);
    void updateStock(String productId, int newStock);
    List<ProductDTO> findByCategory(String categoryName);
    void decrementStock(String productId, int quantity);

}
