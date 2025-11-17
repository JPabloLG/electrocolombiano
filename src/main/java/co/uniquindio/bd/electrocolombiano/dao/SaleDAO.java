package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.SaleDTO;
import co.uniquindio.bd.electrocolombiano.model.Sale;

import java.time.LocalDate;
import java.util.List;

public interface SaleDAO {

    void save(SaleDTO sale);
    void delete(String id);
    void update(SaleDTO sale);
    void findById(String id);
    List<Sale> findAll();
    List<SaleDTO> findByMonth(LocalDate date);
}
