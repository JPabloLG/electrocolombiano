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

    public SaleService(SaleDAO saleDAO, UserDAO userDAO){
        this.saleDAO = saleDAO;
        this.userDAO = userDAO;
    }

    public SaleDTO createSale(SaleDTO sale) throws Exception {
        sale.setId(UUID.randomUUID().toString());
        sale.setDateSale(LocalDate.now());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ProductDTO p : sale.getProducts()) {
            subtotal = subtotal.add(p.getPurchaseValue());
        }
        sale.setSubtotal(subtotal);
        sale.setTotalPrice(subtotal);

        // Guardar la venta en Sale
        saleDAO.save(sale);

        for (ProductDTO p : sale.getProducts()) {
            saleDAO.addProductToSale(sale.getId(), p.getId());
        }

        for (PaymentDTO paymentDTO : sale.getPayments()) {
            PaymentDTO createdPayment = createPayment(paymentDTO);
            saleDAO.addPaymentToSale(sale.getId(), createdPayment.getId());
        }

        return sale;
    }

    private void validateId(String idCustomer) throws Exception {
        userDAO.findByCedula(idCustomer);
        if(userDAO.findByCedula(idCustomer)==null){
            throw new Exception("El cliente no existe");
        }
    }
}
