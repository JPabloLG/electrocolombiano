package co.uniquindio.bd.electrocolombiano.dto;

import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
@Setter
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor

public class InstallmentDTO {

    private String id;
    private int  installmentCount;
    private BigDecimal installmentValue;
    private LocalDate installmentDate;
    private String paymentId;
    private boolean isPaid;

    public InstallmentDTO (String id, int installmentCount, BigDecimal installmentValue, LocalDate installmentDate, String paymentId, boolean isPaid) {

        this.id = id;
        this.installmentCount = installmentCount;
        this.installmentValue = installmentValue;
        this.installmentDate = installmentDate;
        this.paymentId = paymentId;
        this.isPaid = isPaid;
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
