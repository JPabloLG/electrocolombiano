package co.uniquindio.bd.electrocolombiano.model;
import co.uniquindio.bd.electrocolombiano.model.Payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sale {

    private String id;
    private LocalDate saleDate;
    private SystemUser customer;
    private SystemUser employee;
    private List<Payment> payments;
    private List<Product> products;
    private BigDecimal subtotal;
    private BigDecimal totalPrice;
}
