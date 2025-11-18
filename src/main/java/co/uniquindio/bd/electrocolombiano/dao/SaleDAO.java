package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.SaleDTO;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

/**
 * Interfaz DAO para la gestión de ventas
 * Define las operaciones CRUD y consultas específicas para la entidad Sale
 */
public interface SaleDAO {

    void save(SaleDTO sale);
    void update(SaleDTO sale);
    void delete(String id);
    SaleDTO findById(String id);
    List<SaleDTO> getAll();
    boolean exists(String saleId);
    List<SaleDTO> findByCustomerId(String customerId);
    List<SaleDTO> findByEmployeeId(String employeeId);
    List<SaleDTO> findByDateRange(LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalSalesByMonth(int year, int month);
    int countSalesByPaymentType(LocalDate startDate, LocalDate endDate, String paymentType);
    BigDecimal getTotalIVAByQuarter(int year, int quarter);
    int countSalesByMonth(int year, int month);
}
