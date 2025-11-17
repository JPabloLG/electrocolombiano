package co.uniquindio.bd.electrocolombiano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@Builder

public class Installment {

    private String id;
    private int  installmentCount;
    private BigDecimal installmentValue;
    private LocalDate installmentDate;
    private Payment payment;
}

