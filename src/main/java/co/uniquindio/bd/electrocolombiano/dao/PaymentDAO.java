package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.PaymentDTO;
import java.util.List;

public interface PaymentDAO {
    void save(PaymentDTO payment);
    void update(PaymentDTO payment);
    void delete(String id);
    PaymentDTO findById(String id);
    List<PaymentDTO> getAll();
    List<PaymentDTO> findBySaleId(String saleId);
}
