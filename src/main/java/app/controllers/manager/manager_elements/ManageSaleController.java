package app.controllers.manager.manager_elements;

import app.cores.StageManager;
import app.dtos.StatisticProductDto;
import app.entities.Product;
import app.entities.User;
import app.enums.InvoiceSettings;
import app.enums.ViewElementPath;
import app.services.api.OrderService;
import app.services.api.PrinterService;
import app.services.api.StatisticService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.util.Callback;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class ManageSaleController extends BaseManageController {

    private static final String[] AVAILABLE_FILTERS = {"profit", "quantity"}; //popularity filter product by quantity sold descending
    private static final String[] ORDER_STATUS = {"closed", "open", "canceled"};
    private static final String[] RESULT_COUNT = {"5", "10", "15", "20", "25", "All"};
    private static final int COUNT_COLUMNS = 5;
    private static final Double TABLE_DEFAULT_WIDTH = 780.0;
    private static final Double TABLE_OFFSET = 5.0;
    private static final int DAYS_TO_SUBTRACT = 30;


    @FXML private Pane chartAnchor;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private ComboBox<String> orderStatus;
    @FXML private ComboBox<String> filter;
    @FXML private ComboBox<String> resultFilter;

    private StatisticService statisticService;

////////////////////INVOICE/////////////////////
    //test for printing invoice
    private PrinterService printerService;
    @FXML private VBox invoiceVBox;
    private Stage invoiceStage;
/////////////////INVOICE///////////////////

    private UserService userService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageSaleController(StageManager stageManager, StatisticService statisticService, UserService userService, PrinterService printerService) {
        super(stageManager);
        this.statisticService = statisticService;

        this.userService = userService;
        this.printerService = printerService;
    }

    @Override
    public void initialize() {
        this.setComboBoxValue(this.filter, AVAILABLE_FILTERS);
        this.setComboBoxValue(this.orderStatus, ORDER_STATUS);
        this.setComboBoxValue(this.resultFilter, RESULT_COUNT);
        this.setInitialTimePeriod();
        createTable();



    }

    @Override
    public void createTable() {

        this.genericTable = new TableView();
        this.genericTable.getStyleClass().addAll("contentTable");
        this.genericTable.setStyle("-fx-pref-height: 161px");
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = (TABLE_DEFAULT_WIDTH - TABLE_OFFSET)/COUNT_COLUMNS;

        TableColumn<StatisticProductDto, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //sold (quantity)
        TableColumn<StatisticProductDto, BigDecimal> quantityColumn = new TableColumn<>("quantity");
        setColumnProperties(quantityColumn, columnWidth);
        quantityColumn.setCellFactory(col -> new TableCell<StatisticProductDto, BigDecimal>(){
            @Override
            protected  void updateItem(BigDecimal item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    setText( "" + item.intValue());
                }
            }
        });
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("sold"));

        TableColumn<StatisticProductDto, Double> priceColumn = new TableColumn<>("price");
        setColumnProperties(priceColumn, columnWidth);
        priceColumn.setCellFactory(ac-> new TableCell<StatisticProductDto, Double>(){
            @Override
            protected  void updateItem(Double item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    setText(String.format("$ %.2f", item ));
                }
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<StatisticProductDto, Double> costColumn = new TableColumn<>("cost");
        setColumnProperties(costColumn, columnWidth);
        costColumn.setCellFactory(ac-> new TableCell<StatisticProductDto, Double>(){
            @Override
            protected  void updateItem(Double item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    setText(String.format("$ %.2f", item ));
                }
            }
        });
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));


        TableColumn<StatisticProductDto, Double> profitColumn = new TableColumn<>("profit");
        setColumnProperties(profitColumn, columnWidth);
        profitColumn.setCellFactory(ac-> new TableCell<StatisticProductDto, Double>(){
            @Override
            protected  void updateItem(Double item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    if (item < 0){
                        setStyle("-fx-text-fill: red;");
                    }
                    setText(String.format("$ %.2f", item ));
                }
            }
        });
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));


        //add columns to tableView
        this.genericTable.getColumns().addAll(nameColumn, quantityColumn,  priceColumn, costColumn, profitColumn);
        super.getMainContentAnchor().getChildren().add(this.genericTable);
        //show result
        this.showFilteredResult();
    }

    @FXML
    private void showFilteredResult(){
        //get data from database
        ObservableList<StatisticProductDto> quantityFilterOrders = this.getDatabaseData();

        this.genericTable.setItems(quantityFilterOrders);


        createLineChart(quantityFilterOrders, this.getComboBoxValue(this.filter));
    }

    //set default time period - 30 days from current day
    private void setInitialTimePeriod(){
        LocalDate currentDate = LocalDate.now();
        this.startDate.setValue(currentDate.minusDays(DAYS_TO_SUBTRACT));
        this.endDate.setValue(currentDate.plusDays(1));

        this.changeDatePickerFormat(this.startDate, "yyyy-MM-dd");
        this.changeDatePickerFormat(this.endDate, "yyyy-MM-dd");
    }

    //change date format in DatePicker to custom format
    private void changeDatePickerFormat(DatePicker datePicker, String dateFormat){
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null){
                    return "";
                }
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {

                if (dateString == null || dateString.trim().isEmpty()){
                    return null;
                } else

                    return LocalDate.parse(dateString, formatter);
            }
        });
    }

    //get data from database, reverse it and return ObservableList based on the quantity from resultFilter
    private ObservableList<StatisticProductDto> getDatabaseData(){

        Date dateStart = Date.from(this.startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date dateEnd = Date.from(this.endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<StatisticProductDto> availableOrders = this.statisticService.getAllStatisticProducts(dateStart, dateEnd, this.getComboBoxValue(this.orderStatus));
        Collections.reverse(availableOrders);

        String resultFilterData = this.getComboBoxValue(this.resultFilter);
        try {
           int resultsLength = Integer.parseInt(resultFilterData);
           int allResults = availableOrders.size();
           if (allResults - resultsLength > 0){
               availableOrders = availableOrders.subList(0, resultsLength);
           }
        } catch (Exception e) {}

        return FXCollections.observableArrayList(availableOrders);
    }


    private void setComboBoxValue(ComboBox<String> comboBox, String... value) {
        comboBox.getItems().addAll(value);
        comboBox.getSelectionModel().selectFirst();
    }

    private String getComboBoxValue(ComboBox<String> comboBox) {
        return comboBox.getValue();
    }



//    private void createBarChart(ObservableList<StatisticProductDto> availableOrders){
//        //stage.setTitle("Bar Chart Sample");
//        final CategoryAxis xAxis = new CategoryAxis() ;
//        final  NumberAxis yAxis= new NumberAxis();
//        final BarChart bc = new BarChart(xAxis, yAxis);
////        bc.setTitle("Summary");
////        xAxis.setLabel("Country");
////        yAxis.setLabel("Value");
//
//        XYChart.Series<String, Double> series = new XYChart.Series();
//        for (StatisticProductDto productDto : availableOrders) {
//            String name = productDto.getName();
//            Double value = productDto.getProfit();
//            series.getData().add(new XYChart.Data<>(name, value));
//
//        }
////        barChart.getData().add(series);
//
//        bc.setStyle("-fx-pref-height: 302px; -fx-max-height: 302px; -fx-pref-width: 838px; -fx-max-width: 838px;");
//        bc.getData().add(series);
//        bc.setBarGap(150);
//        bc.setCategoryGap(10);
//
//        this.chartAnchor.getChildren().add(bc);
//    }

    private void createLineChart(ObservableList<StatisticProductDto> orders, String title){

        final CategoryAxis xAxis = new CategoryAxis() ;
        final  NumberAxis yAxis= new NumberAxis();
        final LineChart<String,Double> bc = new LineChart(xAxis, yAxis);

        double accumulatedValue = 0.0d;
        XYChart.Series<String, Double> series = new XYChart.Series();
        XYChart.Series<String, Double> accumulated = new XYChart.Series<>();
        series.setName(title);
        accumulated.setName(String.format("accumulated %s", title));
        for (StatisticProductDto productDto : orders) {
            String name = productDto.getName();
            Double value = title.equalsIgnoreCase("profit") ? productDto.getProfit() : productDto.getSold().doubleValue();
            series.getData().add(new XYChart.Data<>(name, value));
            accumulatedValue += value;
            accumulated.getData().add(new XYChart.Data<>(name, accumulatedValue));
        }

        bc.setStyle("-fx-pref-height: 302px; -fx-max-height: 302px; -fx-pref-width: 838px; -fx-max-width: 838px;");
        bc.getData().addAll(series, accumulated);
        this.chartAnchor.getChildren().clear();
        this.chartAnchor.getChildren().add(bc);
    }

    ////////////////////////////////////////////////////START INVOICE////////////////////////////////////////////////////////

    //TODO cancel button to close stage
    @FXML private void makeInvoice(){

        this.invoiceStage = new Stage();
        Parent invoiceParent = super.getStageManager().getPane(ViewElementPath.INVOICE);
        this.invoiceStage.initStyle(StageStyle.UNDECORATED);

        //pop up window must be closed to continue interaction with the program
        this.invoiceStage.initModality(Modality.APPLICATION_MODAL);
        this.invoiceStage.setTitle("Invoice");

        //set scene
        Scene invoiceScene = new Scene(invoiceParent);
        this.invoiceStage.setScene(invoiceScene);
        this.invoiceVBox.setStyle("-fx-padding: 30px 10px 20px 10px; -fx-spacing: 0px; -fx-background-color: white; -fx-font-size: 13px; -fx-text-alignment: left;  -fx-max-width: 155px; -fx-min-width: 155px; -fx-pref-width: 155px;");

        //23 symbols
        this.buildInvoiceHeader();
        this.buildInvoiceBody();
        this.buildInvoiceFooter();
        this.invoiceStage.showAndWait();


    }


    private void buildInvoiceHeader(){

        //TODO change operator
        //String operator = order.getUser.getName;
        //for dev
        String operator = "Stamat";
        ////////////////////////////////
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


    private void buildInvoiceBody(){


        Map<Product, Integer> orders = new LinkedHashMap<>();
        Product newProduct1 = new Product();
        newProduct1.setName("braba");
        newProduct1.setPrice(11.56);
        orders.put(newProduct1, 7);
        Product newProduct2 = new Product();
        newProduct2.setName("lqlqlqlql");
        newProduct2.setPrice(1.56);
        orders.put(newProduct2, 57);
        Product newProduct3 = new Product();
        newProduct3.setName("aaaa");
        newProduct3.setPrice(112.56);
        orders.put(newProduct3, 10);


        for (Map.Entry<Product, Integer> orderEntry : orders.entrySet()) {
            String productName = orderEntry.getKey().getName();
            double productPrice = orderEntry.getKey().getPrice();
            int soldQuantity = orderEntry.getValue();

            HBox cost = this.makeSpaceSeparatedHBox(String.format(InvoiceSettings.PRODUCT_QUANTITY.getSetting(),soldQuantity), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), productPrice));
            HBox sum = this.makeSpaceSeparatedHBox(InvoiceSettings.PRODUCT_TOTAL_TITLE.getSetting(), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), productPrice * soldQuantity));;
            this.invoiceVBox.getChildren().addAll(new Text(productName), cost, sum, this.emptyText());
        }
    }


    private void buildInvoiceFooter(){


        double totalValue = 12312.0345;
        double taxBase = 0.2;
        //prob order number
        int numberInvoice = 1231;
        //time
        LocalTime localTime = LocalTime.now();
        LocalDate dateTime = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(InvoiceSettings.HOUR_MINUTES_PATTERN.getSetting(), Locale.ENGLISH);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(InvoiceSettings.DATE_PATTERN.getSetting(), Locale.ENGLISH);
        //serial number
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern(InvoiceSettings.INVOICE_SERIAL_NUMBER_PATTERN.getSetting());
        String invoiceSerialNumber = df.format(numberInvoice);

        //total
        HBox total = this.makeSpaceSeparatedHBox(InvoiceSettings.TOTAL_TITLE.getSetting(), String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), totalValue) );
        total.setStyle("-fx-font-size: 16px; -fx-text-alignment: right; -fx-font-weight: bold");
        //tax
        HBox tax = this.makeSpaceSeparatedHBox(InvoiceSettings.TAX_TITLE.getSetting(),String.format(InvoiceSettings.PRICE_PATTERN.getSetting(), totalValue*( 1 - taxBase)));
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

    @FXML private void printInvoice(){
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
        this.invoiceStage.close();
    }

    @FXML private void cancelInvoice(){
        this.invoiceStage.close();
    }

 ////////////////////////////////////////////////////END INVOICE////////////////////////////////////////////////////////

}
