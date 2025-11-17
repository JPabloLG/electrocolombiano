package co.uniquindio.bd.electrocolombiano.services;

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

    public PaymentService(PaymentDAO paymentDAO) {
        this.paymentDAO=paymentDAO;
    }

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        paymentDTO.setId(UUID.randomUUID().toString());

        Payment payment = new Payment(
                paymentDTO.getId(),
                paymentDTO.getTotalPrice(),
                paymentDTO.isCredit()
        );

        // Guardar el pago base
        paymentDAO.save(paymentDTO);

        if (paymentDTO.isCredit()) {
            BigDecimal total = paymentDTO.getTotalPrice();
            BigDecimal initialQuote = total.multiply(BigDecimal.valueOf(0.3)); // 30%
            BigDecimal financed = total.multiply(BigDecimal.valueOf(0.7))
                    .multiply(BigDecimal.valueOf(1.05)); // 70% + 5% interés

            // La cantidad de cuotas viene del DTO
            int installmentsCount = paymentDTO.getInstallments() != null
                    ? paymentDTO.getInstallments().size()
                    : 12; // opcional, pero ideal que siempre venga del DTO

            BigDecimal monthlyValue = financed.divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_UP);

            List<InstallmentDTO> installmentDTOs = new ArrayList<>();
            LocalDate dueDate = LocalDate.now().plusMonths(1); // primera cuota dentro de un mes

            // Crear DTOs de cuotas
            for (int i = 0; i < installmentsCount; i++) {
                InstallmentDTO installmentDTO = new InstallmentDTO(
                        UUID.randomUUID().toString(),
                        i + 1,
                        monthlyValue,
                        dueDate.plusMonths(i),
                        paymentDTO.getId()
                );
                installmentDTOs.add(installmentDTO);

                Installment installment = new Installment();
                installment.setId(installmentDTO.getId());
                installment.setInstallmentCount(installmentDTO.getInstallmentCount());
                installment.setInstallmentValue(installmentDTO.getInstallmentValue());
                installment.setInstallmentDate(installmentDTO.getInstallmentDate());
                installment.setPayment(payment);
                installmentDAO.save(installment);
            }

            // Asignar la lista de cuotas al DTO para retornar al cliente
            paymentDTO.setInstallments(installmentDTOs);
        }

        // Retornar el PaymentDTO completo
        return paymentDTO;
    }



}
