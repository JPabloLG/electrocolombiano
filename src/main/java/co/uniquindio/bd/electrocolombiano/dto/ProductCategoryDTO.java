package co.uniquindio.bd.electrocolombiano.dto;

public record ProductCategoryDTO(String categoryName,double iva, double profitMargin) {

    public ProductCategoryDTO{
        if(categoryName == null || categoryName.isBlank())
            throw new IllegalArgumentException("El usuario es obligatorio");

        if( iva<0.1)
            throw new IllegalArgumentException("El valor del IVA fuente es obligatorio");

        if (profitMargin < 0.1)
            throw new IllegalArgumentException("El valor del profitMargin es obligatorio");
    }
}
