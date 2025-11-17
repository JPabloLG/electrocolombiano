package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.SaleDAO;
import co.uniquindio.bd.electrocolombiano.dao.UserDAO;
import co.uniquindio.bd.electrocolombiano.dto.ProductDTO;
import co.uniquindio.bd.electrocolombiano.dto.SaleDTO;
import co.uniquindio.bd.electrocolombiano.model.Product;
import co.uniquindio.bd.electrocolombiano.model.Sale;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SaleService {
    private final SaleDAO saleDAO;
    private final UserDAO userDAO;

    public SaleService(SaleDAO saleDAO, UserDAO userDAO){
        this.saleDAO = saleDAO;
        this.userDAO = userDAO;
    }
    
    public SaleDTO createSale(SaleDTO sale) throws Exception {
        String idCustomer = sale.getCustomerId();
        validateId(idCustomer);
        sale.setId(UUID.randomUUID().toString());
        sale.setDateSale(LocalDate.now());
        //logica extra--//

        if (sale.getProducts() != null && !sale.getProducts().isEmpty()) {
            BigDecimal subtotal = BigDecimal.valueOf(0f);
            for (ProductDTO p : sale.getProducts()) {
                subtotal = subtotal.add(p.getPurchaseValue());
            }
            sale.setSubtotal(subtotal);
            sale.setTotalPrice(subtotal); // por ahora total = subtotal
        }
        saleDAO.save(sale);

        return  new SaleDTO(idCustomer,sale.getDateSale(), sale.getEmployee(), sale.getCustomerId(), sale.getSubtotal(), sale.getTotalPrice());
    }

    private void validateId(String idCustomer) throws Exception {
        userDAO.findByCedula(idCustomer);
        if(userDAO.findByCedula(idCustomer)==null){
            throw new Exception("El cliente no existe");
        }
    }
}
