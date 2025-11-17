package co.uniquindio.bd.electrocolombiano.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)

public class InstallmentDTO {

    private String id;
    private int  installmentCount;
    private BigDecimal installmentValue;
    private LocalDate installmentDate;
    private PaymentDTO payment;

    public InstallmentDTO (String id, int installmentCount, BigDecimal installmentValue, LocalDate installmentDate, PaymentDTO payment) {

        this.id = id;
        this.installmentCount = installmentCount;
        this.installmentValue = installmentValue;
        this.installmentDate = installmentDate;
        this.payment = payment;
        validateFields();
    }

    public void validateFields() {
        if (installmentCount < 0)
            throw new IllegalArgumentException("La cantidad de cuotas debe ser mayor a 0");

        if (installmentValue.compareTo(BigDecimal.valueOf(0)) < 0)
            throw new IllegalArgumentException("El valor de cuotas debe ser mayor a 0");

        if (payment == null)
            throw new IllegalArgumentException("El pago es obligatorio");
    }
}
