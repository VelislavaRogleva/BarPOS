package app.controllers;

import app.cores.StageManager;
import app.entities.Product;
import app.enums.InvoiceSettings;
import app.services.api.PrinterService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class InvoiceController implements FxmlController {

    private static final String INVOICE_VBOX_STYLE = "invoiceVBoxStyle";
    private static final double TAX_BASE = 0.2;

    @FXML private VBox invoiceVBox;

    private StageManager stageManager;
    private PrinterService printerService;
    private Stage invoiceStage;
    private double total;
    private boolean okPayment = false;

    @Autowired
    @Lazy
    public InvoiceController(StageManager stageManager, PrinterService printerService) {
        this.stageManager = stageManager;
        this.printerService = printerService;
    }

    @Override
    public void initialize() {

    }

    public void buildInvoice(Stage invoiceStage, String operator, Map<Product, Integer> products, Long id){
        this.invoiceStage = invoiceStage;

        this.invoiceVBox.getStyleClass().add(INVOICE_VBOX_STYLE);

        this.buildInvoiceHeader(operator);
        this.buildInvoiceBody(products);
        this.buildInvoiceFooter(id);

    }

    public boolean isInvoicePaid(){
        return this.okPayment;
    }

    @FXML
    private void printInvoice(){
        String printingError;
        if (null != this.invoiceVBox) {
            printingError = this.printerService.printNode(this.invoiceVBox);
        } else {
            printingError = "Cannot print empty invoice";
        }
        if (!printingError.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Printing Error");
            alert.setHeaderText("Unable to print the document");
            alert.setContentText(printingError);

            alert.showAndWait();
        }
        this.okPayment = true;
        this.invoiceStage.close();
    }

    @FXML
    private void cancelInvoice(){
        this.okPayment = false;
        this.invoiceStage.close();
    }


    private void buildInvoiceHeader(String operator){

        HBox title = new HBox();
        title.setAlignment(Pos.CENTER);
        Text invoiceTitle = new Text(InvoiceSettings.COMPANY_TITLE.getSetting());
        title.getChildren().add(invoiceTitle);
        Text mainAddress = new Text(InvoiceSettings.BAR_ADDRESS.getSetting());
        mainAddress.setStyle("-fx-wrap-text: true;");
        Label barInfo = new Label(InvoiceSettings.BAR_FINANCIAL_INFO.getSetting());
        barInfo.setStyle("-fx-wrap-text: true;");
        Label operatorInfo = new Label(String.format(InvoiceSettings.OPERATOR_INFO.getSetting(),operator));
        barInfo.setStyle("-fx-wrap-text: true; -fx-text-alignment: left;");

        this.invoiceVBox.getChildren().addAll(title, this.emptyText(), mainAddress, this.emptyText(), barInfo, operatorInfo, this.emptyText());
    }


    private void buildInvoiceBody(Map<Product, Integer> products){
        this.total = 0.0d;
        for (Map.Entry<Product, Integer> productEntry : products.entrySet()) {
            String productName = productEntry.getKey().getName();
            double productPrice = productEntry.getKey().getPrice();
            int soldQuantity = productEntry.getValue();

            HBox cost = this.makeSpaceSeparatedHBox(String.format(InvoiceSettings.PRODUCT_QUANTITY.getSetting(),soldQuantity), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), productPrice));
            double productTotal = productPrice * soldQuantity;
            HBox sum = this.makeSpaceSeparatedHBox(InvoiceSettings.PRODUCT_TOTAL_TITLE.getSetting(), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), productTotal));;
            this.invoiceVBox.getChildren().addAll(new Text(productName), cost, sum, this.emptyText());
            this.total += productTotal;
        }
    }


    private void buildInvoiceFooter(Long id){

        //time
        LocalTime localTime = LocalTime.now();
        LocalDate dateTime = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(InvoiceSettings.HOUR_MINUTES_PATTERN.getSetting(), Locale.ENGLISH);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(InvoiceSettings.DATE_PATTERN.getSetting(), Locale.ENGLISH);
        //serial number
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern(InvoiceSettings.INVOICE_SERIAL_NUMBER_PATTERN.getSetting());
        String invoiceSerialNumber = df.format(id);

        //total
        HBox total = this.makeSpaceSeparatedHBox(InvoiceSettings.TOTAL_TITLE.getSetting(), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), this.total) );
        total.setStyle("-fx-font-size: 16px; -fx-text-alignment: right; -fx-font-weight: bold");
        //tax
        HBox tax = this.makeSpaceSeparatedHBox(InvoiceSettings.TAX_TITLE.getSetting(),String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), this.total *  TAX_BASE));
        //serial number
        Text invoiceNumber = new Text(invoiceSerialNumber);
        //date time
        HBox date = this.makeSpaceSeparatedHBox(dateTime.format(dateFormat), localTime.format(timeFormatter));
        //bottom text
        HBox invoiceEnd = this.makeSpaceSeparatedHBox(InvoiceSettings.RECEIPT_COUNTRY.getSetting(), InvoiceSettings.RECEIPT_TYPE.getSetting());

        this.invoiceVBox.getChildren().addAll(total, this.emptyText(), tax, invoiceNumber, this.emptyText(), date, invoiceEnd);
    }

    private Text emptyText(){
        return new Text("");
    }

    private Region makeSeparator(){
        Region separator = new Region();
        HBox.setHgrow(separator, Priority.ALWAYS);
        separator.setMinWidth(Region.USE_PREF_SIZE);
        return separator;
    }

    private HBox makeSpaceSeparatedHBox(String leftText, String rightText){
        HBox newHBox = new HBox();
        Region spacerEnd = this.makeSeparator();
        newHBox.getChildren().addAll(new Text(leftText), spacerEnd, new Text(rightText));
        return newHBox;
    }

}
