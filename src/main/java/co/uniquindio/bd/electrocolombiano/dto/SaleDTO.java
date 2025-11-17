package co.uniquindio.bd.electrocolombiano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class SaleDTO {

    private String id;
    private LocalDate dateSale;
    private UserDTO employee;
    private String customerId;

    @Builder.Default
    private List<PaymentDTO> payments = new ArrayList<>();

    @Builder.Default
    private List<ProductDTO> products = new ArrayList<>();

    private BigDecimal totalPrice;
    private BigDecimal subtotal;

    // Constructor personalizado (el que estabas usando)
    public SaleDTO(String id, LocalDate dateSale, UserDTO employee, String customerId,
                   BigDecimal subtotal, BigDecimal totalPrice) {
        this.id = id;
        this.dateSale = dateSale;
        this.employee = employee;
        this.customerId = customerId;
        this.payments = new ArrayList<>();
        this.products = new ArrayList<>();
        this.subtotal = subtotal;
        this.totalPrice = totalPrice;

        validate();
    }

    // Método de validación separado
    private void validate() {
        if (employee == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("La cédula del cliente es obligatoria");
        }
    }
}