package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.ProductCategoryDAO;
import co.uniquindio.bd.electrocolombiano.dao.ProductDAO;
import co.uniquindio.bd.electrocolombiano.dto.ProductCategoryDTO;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService {

    private final ProductDAO productDAO;
    public  final ProductCategoryDAO productCategoryDAO;

    public ProductService(ProductDAO productDAO, ProductCategoryDAO productCategoryDAO) {
        this.productDAO= productDAO;
        this.productCategoryDAO = productCategoryDAO;
    }

    public ProductDTO createProduct(ProductDTO createProductDTO){
        createProductDTO.setId(UUID.randomUUID().toString());

        ProductDTO product = new ProductDTO(createProductDTO.getId(), createProductDTO.getUnitPrice(),
                createProductDTO.getPurchaseValue(), createProductDTO.getStock(), createProductDTO.getName(),
                createProductDTO.getCategory());
        productDAO.save(product);
        return product;
    }

    public List<ProductDTO> getAllProducts(){
        List <ProductDTO> products = productDAO.getAll();
        if(products.isEmpty()){
            throw new RuntimeException("No se encontraron productos");
        }
        return productDAO.getAll();
    }

    public ProductCategoryDTO getCategory(String categoryName){
        ProductCategoryDTO productCategoryDTO = productCategoryDAO.findByName(categoryName);
        if(productCategoryDTO == null){
            throw new RuntimeException("Category not found");
        }
        return productCategoryDTO;
    }
    
    public ProductDTO findByName(String productName){
        ProductDTO productDTO = productDAO.findByName(productName);
        if(productDTO==null){
            throw new RuntimeException("Product not found");
        }
        return productDTO;
    }
}
