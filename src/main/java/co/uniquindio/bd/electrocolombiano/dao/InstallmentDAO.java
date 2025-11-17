package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.InstallmentDTO;
import java.util.List;

public interface InstallmentDAO {
    void save(InstallmentDTO installment);
    void update(InstallmentDTO installment);
    void delete(String id);
    InstallmentDTO findById(String id);
    List<InstallmentDTO> getAll();
    List<InstallmentDTO> findByPaymentId(String paymentId);
}
