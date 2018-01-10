package app.services.api;

import app.entities.Product;

import java.util.Map;

public interface InvoiceService {
    void makeInvoice(String operator, Map<Product, Integer> products, Long id);

    boolean isInvoicePaid();
}
