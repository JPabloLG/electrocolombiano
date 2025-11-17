package co.uniquindio.bd.electrocolombiano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor

public class Installment {

    private String id;
    private int  installmentCount;
    private BigDecimal installmentValue;
    private LocalDate installmentDate;
    private Payment payment;
}

