package co.uniquindio.bd.electrocolombiano.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)

public class InstallmentDTO {

    private String id;
    private int  installmentCount;
    private BigDecimal installmentValue;
    private LocalDate installmentDate;
    @Setter
    private String paymentId;

    public InstallmentDTO (String id, int installmentCount, BigDecimal installmentValue, LocalDate installmentDate, String paymentId) {

        this.id = id;
        this.installmentCount = installmentCount;
        this.installmentValue = installmentValue;
        this.installmentDate = installmentDate;
        this.paymentId = paymentId;
        validateFields();
    }

    public void validateFields() {
        if (installmentCount < 0)
            throw new IllegalArgumentException("La cantidad de cuotas debe ser mayor a 0");

        if (installmentValue.compareTo(BigDecimal.valueOf(0)) < 0)
            throw new IllegalArgumentException("El valor de cuotas debe ser mayor a 0");

        if (paymentId == null)
            throw new IllegalArgumentException("El id del pago es obligatorio");
    }
}
