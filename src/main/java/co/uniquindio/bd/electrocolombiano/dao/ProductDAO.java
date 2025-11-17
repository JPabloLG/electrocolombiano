package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;

import java.util.List;

public interface ProductDAO {

    void save(ProductDTO product);
    void update(ProductDTO product);
    void delete(int id);
    ProductDTO findById(String id);
    List<ProductDTO> getAll();
    boolean exists(String productId);
    List<ProductDTO> findByCategory(int categoryId);

}
