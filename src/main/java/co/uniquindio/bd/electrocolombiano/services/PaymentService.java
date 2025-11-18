package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.InstallmentDAO;
import co.uniquindio.bd.electrocolombiano.dao.PaymentDAO;
import co.uniquindio.bd.electrocolombiano.dto.InstallmentDTO;
import co.uniquindio.bd.electrocolombiano.dto.PaymentDTO;
import co.uniquindio.bd.electrocolombiano.model.Installment;
import co.uniquindio.bd.electrocolombiano.model.Payment;
import co.uniquindio.bd.electrocolombiano.model.PaymentCredit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final InstallmentDAO installmentDAO;

    public PaymentService(PaymentDAO paymentDAO, InstallmentDAO installmentDAO) {
        this.paymentDAO=paymentDAO;
        this.installmentDAO=installmentDAO;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {

        System.out.println("=== DEPURACIÓN PAYMENT SERVICE ===");
        System.out.println("Payment ID: " + paymentDTO.getId());
        System.out.println("Total Price: " + paymentDTO.getTotalPrice());
        System.out.println("Is Credit: " + paymentDTO.isCredit());
        System.out.println("Installment Count: " + paymentDTO.getInstallmentCount());
        System.out.println("Installments List Size: " + (paymentDTO.getInstallments() != null ? paymentDTO.getInstallments().size() : "null"));
        System.out.println("===================================");

        if (paymentDTO.getTotalPrice() == null ||
                paymentDTO.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio total debe ser mayor a 0");
        }

        paymentDAO.save(paymentDTO);

        // === PAGO DE CONTADO ===
        if (!paymentDTO.isCredit()) {
            return paymentDTO;
        }

        int installmentsCount = paymentDTO.getInstallmentCount();

        if (installmentsCount <= 0) {
            throw new IllegalArgumentException("El número de cuotas debe ser mayor a 0 para pagos a crédito.");
        }

        BigDecimal total = paymentDTO.getTotalPrice();
        BigDecimal initialQuote = total.multiply(BigDecimal.valueOf(0.30)); // 30%
        BigDecimal financed = total
                .multiply(BigDecimal.valueOf(0.70))   // 70%
                .multiply(BigDecimal.valueOf(1.05));  // +5% interés

        BigDecimal monthlyValue = financed.divide(
                BigDecimal.valueOf(installmentsCount),
                2,
                RoundingMode.HALF_UP
        );

        List<InstallmentDTO> installmentDTOs = new ArrayList<>();
        LocalDate baseDate = LocalDate.now().plusMonths(1);

        for (int i = 0; i < installmentsCount; i++) {

            InstallmentDTO installmentDTO = new InstallmentDTO(
                    UUID.randomUUID().toString(),
                    i + 1,
                    monthlyValue,
                    baseDate.plusMonths(i),
                    paymentDTO.getId(),
                    false
            );

            installmentDTOs.add(installmentDTO);

            installmentDAO.save(installmentDTO);
        }

        paymentDTO.setInstallments(installmentDTOs);

        return paymentDTO;
    }



}
