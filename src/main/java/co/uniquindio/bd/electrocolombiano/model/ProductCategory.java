package co.uniquindio.bd.electrocolombiano.model;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCategory {

    private int id;
    private String categoryName;
    private double iva;
    private double profitMargin;


}

