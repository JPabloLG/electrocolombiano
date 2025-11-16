package co.uniquindio.bd.electrocolombiano.model;
import co.uniquindio.bd.electrocolombiano.model.Payment;

import lombok.*;

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
    private SystemUser user;
    private List<Product> products;
    private List<Payment> pago;
}
