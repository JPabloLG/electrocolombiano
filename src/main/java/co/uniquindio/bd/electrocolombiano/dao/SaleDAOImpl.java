package co.uniquindio.bd.electrocolombiano.dao;

import co.uniquindio.bd.electrocolombiano.dto.SaleDTO;
import co.uniquindio.bd.electrocolombiano.model.Sale;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class SaleDAOImpl implements SaleDAO {

    private final Connection connection;

    public SaleDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SaleDTO sale){

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public void update(SaleDTO sale) {

    }

    @Override
    public void findById(String id) {

    }

    @Override
    public List<Sale> findAll() {
        return List.of();
    }

    @Override
    public List<SaleDTO> findByMonth(LocalDate date) {
        return List.of();
    }
}
