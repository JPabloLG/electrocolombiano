package co.uniquindio.bd.electrocolombiano.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class PaymentCredit {

    private List<Installment> installments;
    private Payment payment;

}

