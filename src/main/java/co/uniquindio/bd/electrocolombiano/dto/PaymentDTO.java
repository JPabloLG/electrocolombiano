package co.uniquindio.bd.electrocolombiano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
public class PaymentDTO {

    private String id;
    private BigDecimal totalPrice;
    private boolean isCredit;
    private String saleId;
    private String installmentCount;

    @Builder.Default
    private List<InstallmentDTO> installments = new ArrayList<>();

    public PaymentDTO(String id, BigDecimal totalPrice, boolean isCredit, String saleId) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.isCredit = isCredit;
        this.installments = new ArrayList<>();
        this.saleId = saleId;
        validateFields();
    }

    public void validateFields() {
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio total debe ser mayor a 0");
        }
    }

    // Métodos de utilidad
    public void addInstallment(InstallmentDTO installment) {
        if (this.installments == null) {
            this.installments = new ArrayList<>();
        }
        this.installments.add(installment);
    }

    public void removeInstallment(InstallmentDTO installment) {
        if (this.installments != null) {
            this.installments.remove(installment);
        }
    }

    public int getInstallmentCount() {
        return installments != null ? installments.size() : 0;
    }
}