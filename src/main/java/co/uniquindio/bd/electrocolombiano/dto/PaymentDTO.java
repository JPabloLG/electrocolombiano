package co.uniquindio.bd.electrocolombiano.dto;


import co.uniquindio.bd.electrocolombiano.model.Installment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter

public class PaymentDTO {

    private  String id;
    private BigDecimal totalPrice;
    private  boolean isCredit;

    @Builder.Default
    private List<InstallmentDTO> installments = new ArrayList<>();

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
