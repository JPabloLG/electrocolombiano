package co.uniquindio.bd.electrocolombiano.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder(toBuilder = true)

public class PaymentDTO {

    private  String id;
    private BigDecimal totalPrice;
    private  boolean isCredit;

    public PaymentDTO (String id, BigDecimal totalPrice,  boolean isCredit){

        this.id = id;
        this.totalPrice = totalPrice;
        this.isCredit = isCredit;
        validateFields();
    }

    public void validateFields() {
        if (totalPrice.compareTo(BigDecimal.valueOf(0)) < 0)
            throw new IllegalArgumentException("El precio total debe ser mayor a 0");
    }
}
