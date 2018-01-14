package app.controllers.sale;

import app.controllers.FxmlController;
import app.controllers.SaleController;
import app.dtos.OrderDto;
import app.entities.Product;
import app.services.api.InvoiceService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class PayViewController implements FxmlController {
    @FXML
    private Label payViewTotalSum, invalidInputLabel, payViewCash, payViewTax, payViewChange;
    @FXML
    private GridPane payViewGridPane;

    private OrderDto orderDto;
    private InvoiceService invoiceService;
    private SaleController saleController;

    @Autowired
    @Lazy
    public PayViewController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public void initialize() {
        createNumPadButtonForPayView();
        this.invalidInputLabel.setVisible(false);
    }

    public void setPayViewTotalSum(String payViewTotalSum) {
        this.payViewTotalSum.setText(payViewTotalSum);
    }

    public void setPayViewTax(String payViewTax) {
        this.payViewTax.setText(payViewTax);
    }

    public void setOrderDto(OrderDto orderDto) {
        this.orderDto = orderDto;
    }

    public void setSaleController(SaleController saleController) {
        this.saleController = saleController;
    }

    @FXML
    public void payViewMakePaymentHandler() {
        if (this.payViewCash.getText().isEmpty() ||
                (Double.parseDouble(this.payViewTotalSum.getText())) > Double.parseDouble(this.payViewCash.getText())) {
            this.invalidInputLabel.setVisible(true);
        } else if (this.orderDto != null) {

            //add check for isInvoicePaid
            try {
                String operator = this.orderDto.getUser().getName();
                Map<Product, Integer> products = this.orderDto.getProducts();
                long id = this.orderDto.getOrderId();
                this.invoiceService.makeInvoice(operator, products, id);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Invoice");
                alert.setHeaderText("Invalid Invoice information");
                alert.setContentText("Please correct invoice information!");

                alert.showAndWait();
            }

            if (this.invoiceService.isInvoicePaid()) {
                this.saleController.payViewMakePayment();
            }
        }
    }

    @FXML
    private void payViewCancelHandler() {
        this.saleController.payViewCancelPayment();
    }

    private void createNumPadButtonForPayView() {
        int digitCounter = 1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button digitNumPadButton = new Button();
                digitNumPadButton.setText(String.valueOf(digitCounter));
                digitNumPadButton.getStyleClass().add("payViewNumPadButtons");
                digitNumPadButton.setOnAction(e -> {
                    if (checkValidNumberInput()) {
                        this.payViewCash.setText(this.payViewCash.getText() + digitNumPadButton.getText());
                        this.payViewChange.setText(String.format(Locale.US, "%.2f",
                                Double.parseDouble(this.payViewCash.getText()) -
                                        Double.parseDouble(this.payViewTotalSum.getText())));
                    }
                });
                this.payViewGridPane.add(digitNumPadButton, j, i);
                digitCounter++;
            }
        }

        Button dotNumPadButton = new Button();
        dotNumPadButton.getStyleClass().add("payViewNumPadButtons");
        dotNumPadButton.setText(".");
        dotNumPadButton.setOnAction(e -> {
            if (checkValidDotInput()) {
                this.payViewCash.setText(this.payViewCash.getText() + dotNumPadButton.getText());
            }
        });
        this.payViewGridPane.add(dotNumPadButton, 0, 3);

        Button zeroNumPadButton = new Button();
        zeroNumPadButton.getStyleClass().add("payViewNumPadButtons");
        zeroNumPadButton.setText("0");
        zeroNumPadButton.setOnAction(e -> {
            if (checkValidNumberInput()) {
                this.payViewCash.setText(this.payViewCash.getText() + zeroNumPadButton.getText());
                this.payViewChange.setText(String.format(Locale.US, "%.2f", Double.parseDouble(this.payViewCash.getText()) -
                        Double.parseDouble(this.payViewTotalSum.getText())));
            }
        });
        this.payViewGridPane.add(zeroNumPadButton, 1, 3);

        Button deleteNumPadButton = new Button();
        deleteNumPadButton.getStyleClass().add("payViewNumPadButtons");
        deleteNumPadButton.setText("X");
        deleteNumPadButton.setOnAction(e -> {
            if (!this.payViewCash.getText().isEmpty()) {
                this.invalidInputLabel.setVisible(false);
                if (this.payViewCash.getText().length() == 1) {
                    this.payViewCash.setText("");
                    this.payViewChange.setText("0.00");
                } else {
                    this.payViewCash.setText(this.payViewCash.getText()
                            .substring(0, this.payViewCash.getText().length() - 1));
                    this.payViewChange.setText(String.format(Locale.US, "%.2f",
                            Double.parseDouble(this.payViewCash.getText()) -
                                    Double.parseDouble(this.payViewTotalSum.getText())));
                }
            }
        });
        this.payViewGridPane.add(deleteNumPadButton, 2, 3);
    }

    private boolean checkValidNumberInput() {
        String amount = this.payViewCash.getText();
        if (amount.isEmpty()) {
            this.invalidInputLabel.setVisible(false);
            return true;
        }
        if (amount.charAt(0) == '0' && amount.length() == 1) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        if (amount.length() == 9) {
            return false;
        }
        this.invalidInputLabel.setVisible(false);
        return true;
    }

    private boolean checkValidDotInput() {
        String amount = this.payViewCash.getText();
        if (amount.isEmpty()) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        if (amount.contains(".")) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        this.invalidInputLabel.setVisible(false);
        return true;
    }
}

