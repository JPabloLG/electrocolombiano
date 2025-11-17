package co.uniquindio.bd.electrocolombiano.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    private String id;
    private BigDecimal unitPrice;
    private BigDecimal purchaseValue;
    private int stock;
    private ProductCategory category;

}
