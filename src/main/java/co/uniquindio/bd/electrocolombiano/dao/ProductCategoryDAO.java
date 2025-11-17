package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;

import java.util.List;

public interface ProductCategoryDAO {

    void save(ProductCategoryDTO productCategory);
    void update(ProductCategoryDTO productCategory);
    void delete(int id);
    ProductCategoryDTO findById(int id);
    ProductCategoryDTO findByName(String categoryName);
    List<ProductCategoryDTO> getAll();
    List<ProductCategoryDTO> getCategoriesWithHighestIva(int limit);
    boolean exists(int categoryId);
    int getProductCount(int categoryId);
    boolean existsByName(String categoryName);
    List<ProductCategoryDTO> getCategoriesWithHighestProfitMargin(int limit);

}
