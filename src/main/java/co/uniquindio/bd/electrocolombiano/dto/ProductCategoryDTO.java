package co.uniquindio.bd.electrocolombiano.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)

public class ProductCategoryDTO {
    private int id;
    private String categoryName;
    private BigDecimal iva;
    private BigDecimal profitMargin;

    private ProductCategoryDTO(int id, String categoryName, BigDecimal iva, BigDecimal profitMargin) {
        this.id = id;
        this.categoryName = categoryName;
        this.iva = iva;
        this.profitMargin = profitMargin;

        validate();
    }

    private void validate() {

        if (categoryName == null || categoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        if (iva.compareTo(BigDecimal.valueOf(0)) < 0.0) {
            throw new IllegalArgumentException("El IVA no puede ser negativo");
        }

        if (profitMargin.compareTo(BigDecimal.valueOf(0)) < 0.0) {
            throw new IllegalArgumentException("El margen de utilidad no puede ser negativo");
        }
    }
}
