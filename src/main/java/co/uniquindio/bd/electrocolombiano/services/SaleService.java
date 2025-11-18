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

        /**
         * Crea una venta SIN crear pagos automáticamente
         * Los pagos se crean desde el controlador
         */
        public SaleDTO createSale(SaleDTO sale) {
            try {
                // Generar ID único
                String saleId = "SALE-" + System.currentTimeMillis();
                sale.setId(saleId);

                // Establecer fecha actual
                sale.setDateSale(LocalDate.now());

                System.out.println("=== GUARDANDO VENTA ===");
                System.out.println("ID: " + saleId);
                System.out.println("Empleado: " + sale.getEmployee().getCedula());
                System.out.println("Cliente: " + sale.getCustomerId());
                System.out.println("Total: " + sale.getTotalPrice());
                System.out.println("Productos: " + sale.getProducts().size());
                System.out.println("=======================");

                // Guardar la venta (SIN pagos)
                saleDAO.save(sale);

                System.out.println("✓ Venta guardada exitosamente");

                return sale;

            } catch (Exception e) {
                System.err.println("✗ Error al crear venta: " + e.getMessage());
                throw new RuntimeException("Error al crear la venta: " + e.getMessage(), e);
            }
        }

    public BigDecimal countSaleByMonth(int month, int year) throws Exception {
        return saleDAO.getTotalSalesByMonth(year, month);
    }

    public BigDecimal fingIVAPay(int year, int cuarter) throws Exception {
        return  saleDAO.getTotalIVAByQuarter(year, cuarter);
    }

    private void validateId(String idCustomer) throws Exception {
        userDAO.findByCedula(idCustomer);
        if(userDAO.findByCedula(idCustomer)==null){
            throw new Exception("El cliente no existe");
        }
    }
}
