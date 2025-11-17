package co.uniquindio.bd.electrocolombiano.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder(toBuilder = true)

public class ProductDTO {

    private String id;
    private BigDecimal unitPrice;
    private BigDecimal purchaseValue;
    private int stock;
    private ProductCategoryDTO category;

    public ProductDTO (String id, BigDecimal unitPrice, BigDecimal purchaseValue, int stock, ProductCategoryDTO category){
        if (unitPrice.compareTo(BigDecimal.valueOf(0)) <= 0)
            throw new IllegalArgumentException("El valor por cada unidad debe ser mayor a 0");

        if (unitPrice.compareTo(BigDecimal.valueOf(0)) <= 0.0)
            throw new IllegalArgumentException("El valor de compra debe ser mayor a 0");

        if (stock < 0)
            throw new IllegalArgumentException("El stock del producto debe ser mayor a 0");

        if(category == null)
            throw new IllegalArgumentException("La categoria del producto es obligatoria");

        this.id = id;
        this.unitPrice = unitPrice;
        this.purchaseValue = purchaseValue;
        this.stock = stock;
        this.category = category;
    }
}
