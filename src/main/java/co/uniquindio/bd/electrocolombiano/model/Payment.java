package co.uniquindio.bd.electrocolombiano.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Payment {

    private  String id;
    private BigDecimal totalPrice;
    private  boolean isCredit;

}