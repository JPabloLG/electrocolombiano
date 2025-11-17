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


        // 2. Convertir DTO → Entidad Payment
        Payment payment = new Payment(
                paymentDTO.getId(),
                paymentDTO.getTotalPrice(),
                paymentDTO.isCredit(),
                paymentDTO.getSaleId()
        );

        // 3. Guardar Payment en BD
        paymentDAO.save(paymentDTO);

        // 4. Si NO es crédito → terminar
        if (!paymentDTO.isCredit()) {
            return paymentDTO;
        }

        // ==== 5. Si es crédito, generar cuotas ====
        BigDecimal total = paymentDTO.getTotalPrice();
        BigDecimal initialQuote = total.multiply(BigDecimal.valueOf(0.30)); // 30%
        BigDecimal financed = total.multiply(BigDecimal.valueOf(0.70))      // 70%
                .multiply(BigDecimal.valueOf(1.05));    // +5% intereses

        int installmentsCount = paymentDTO.getInstallmentCount(); // <-- debe venir en el DTO
        BigDecimal monthlyValue = financed.divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_UP);

        List<InstallmentDTO> installmentDTOs = new ArrayList<>();
        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 0; i < installmentsCount; i++) {


            InstallmentDTO installmentDTO = new InstallmentDTO(
                    UUID.randomUUID().toString(),
                    i + 1,
                    monthlyValue,
                    dueDate.plusMonths(i),
                    paymentDTO.getId() 
            );

            installmentDTOs.add(installmentDTO);
            // 5.3 Guardar cuota en la BD
            installmentDAO.save(installmentDTO);
        }
        paymentDTO.setInstallments(installmentDTOs);
        return paymentDTO;
    }



}
