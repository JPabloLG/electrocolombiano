package co.uniquindio.bd.electrocolombiano.services;

import co.uniquindio.bd.electrocolombiano.dao.SaleDAO;
import co.uniquindio.bd.electrocolombiano.dao.UserDAO;
import co.uniquindio.bd.electrocolombiano.dto.PaymentDTO;
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
    private final PaymentService paymentService;

    public SaleService(SaleDAO saleDAO, UserDAO userDAO, PaymentService paymentService) {
        this.saleDAO = saleDAO;
        this.userDAO = userDAO;
        this.paymentService = paymentService;
    }

    public SaleDTO createSale(SaleDTO sale) throws Exception {
        sale.setId(UUID.randomUUID().toString());
        sale.setDateSale(LocalDate.now());
        validateId(sale.getCustomerId());
        sale.setEmployee(sale.getEmployee());
        sale.setCustomerId(sale.getCustomerId());
        PaymentDTO paymentDTO = new PaymentDTO(UUID.randomUUID().toString(),sale.getTotalPrice(), sale.isCredit() , sale.getId());
        saleDAO.save(sale);
        paymentService.createPayment(paymentDTO);
        sale.setPayments(sale.getPayments());
        return sale;
    }

    private void validateId(String idCustomer) throws Exception {
        userDAO.findByCedula(idCustomer);
        if(userDAO.findByCedula(idCustomer)==null){
            throw new Exception("El cliente no existe");
        }
    }
}
