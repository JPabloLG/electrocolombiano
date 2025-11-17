package co.uniquindio.bd.electrocolombiano.model;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCategory {

    private int id;
    private String categoryName;
    private BigDecimal iva;
    private BigDecimal profitMargin;


}

