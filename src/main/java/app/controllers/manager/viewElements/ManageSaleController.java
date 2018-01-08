package app.controllers.manager.viewElements;

import app.cores.StageManager;
import app.entities.Product;
import app.entities.User;
import app.enums.InvoiceSettings;
import app.enums.ViewElementPath;
import app.services.api.PrinterService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
public class ManageSaleController extends BaseManageController {

    private static final String[] AVAILABLE_FILTERS = {"overall", "popularity", "day of Week", "category"}; //popularity filter product by quantity sold descending
    private static final String[] ORDER_STATUS = {"open", "close", "canceled"};
    private static final int OBJECT_COUNT_PROPERTIES = 2;



    @FXML private Pane chartAnchor;
    @FXML private Chart chart;
    @FXML private ComboBox<String> orderStatus;
    @FXML private ComboBox<String> filters;


    //test for printing invoice
    private PrinterService printerService;
    @FXML private VBox invoiceVBox;
    private Stage invoiceStage;
////////////////////////////////////

    private UserService userService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageSaleController(StageManager stageManager, UserService userService, PrinterService printerService) {
        super(stageManager);
        this.userService = userService;
        this.printerService = printerService;
    }

    @Override
    public void initialize() {
        addFilters();
        createTable();
    //    super.addButtonAction(this.genericTable);


    }

//    ///////////////////////// dev creating fake database entries ////////////////////////////////
//    // Set
//    protected <S> ObservableList<S> getAllFakeCategories(){
//        ObservableList<S> categories = FXCollections.observableArrayList();
//
//        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey"};
//        Long id =1L;
//
//        for (String category:fakeCategories) {
//            User newCat = new User();
//            newCat.setId(id);
//            newCat.setName(category);
//            newCat.setPasswordHash(this.categoryService.hashPassKey(String.format("bobotopop%d",id)));
//            Role role0 = new Role();
//            role0.setId(1111L);
//            String roleString = id % 2 == 0 ? "MANAGER" : "WAITER";
//            role0.setRole(roleString);
//            newCat.setRole(role0);
//            categories.add((S) newCat);
//            id++;
//        }
//        return categories;
//    }
//    /////////////////////////////////////////////////////////////////////////////////////////////////



    @Override
    void createTable() {

        this.genericTable = new TableView();
        this.genericTable.getStyleClass().addAll("contentTable");
        this.genericTable.setStyle("-fx-pref-height: 161px");
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = super.calculateColumnWidth(OBJECT_COUNT_PROPERTIES);

        //TODO
        // if filter by overall , day of week - day, if filter by popularity, category - name
        TableColumn<User, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        //TODO
        //quantity sold column
        TableColumn<User, String> quantityColumn = new TableColumn<>("quantity");
        setColumnProperties(nameColumn, columnWidth);
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));

        //TODO
        //average price per unit in $
        TableColumn<User, String> avgPriceColumn = new TableColumn<>("AVG price");
        setColumnProperties(nameColumn, columnWidth);
        avgPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        avgPriceColumn.setCellValueFactory(new PropertyValueFactory<>("avgPrice"));

        //TODO
        //average cost per unit in $
        TableColumn<User, String> avgCostColumn = new TableColumn<>("AVG cost");
        setColumnProperties(nameColumn, columnWidth);
        avgCostColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        avgCostColumn.setCellValueFactory(new PropertyValueFactory<>("avgCost"));

        //TODO
        //Profit in $
        TableColumn<User, String> profitColumn = new TableColumn<>("profit");
        setColumnProperties(nameColumn, columnWidth);
        profitColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));


        //add columns to tableView
        this.genericTable.getColumns().addAll(nameColumn, quantityColumn, avgPriceColumn, avgCostColumn, profitColumn);

        // fetch from database
        ObservableList<User> availableEmployees = FXCollections.observableArrayList(this.userService.getAllRegisteredUsers());

        //get from fakeLand
        //ObservableList<User> availableEmployees = getAllFakeCategories();
        if (availableEmployees.size()>0) {
            this.genericTable.setItems(availableEmployees);
            super.getMainContentAnchor().getChildren().add(this.genericTable);
        }
    }

    private void addFilters() {
        this.filters.getItems().addAll(AVAILABLE_FILTERS);
        this.filters.getSelectionModel().selectFirst();
    }

    private void addOrderStatus() {
        this.orderStatus.getItems().addAll(ORDER_STATUS);
        this.orderStatus.getSelectionModel().selectFirst();
    }


    //////////////////////////////////Invoice test/////////////////////////////////////////

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
//        Text Separator = new Text("");
        Text mainAddress = new Text(InvoiceSettings.BAR_ADDRESS.getSetting());
        mainAddress.setStyle("-fx-wrap-text: true;");
        Label barInfo = new Label(InvoiceSettings.BAR_FINANCIAL_INFO.getSetting());
        barInfo.setStyle("-fx-wrap-text: true;");
        Label operatorInfo = new Label(String.format(InvoiceSettings.OPERATOR_INFO.getSetting(),operator));
        barInfo.setStyle("-fx-wrap-text: true; -fx-text-alignment: left;");
        //       invoiceTitle.setStyle("-fx-translate-x: 70px; -fx-translate-y: 10px");
        this.invoiceVBox.getChildren().addAll(title, mainAddress, barInfo, operatorInfo);
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

            Text name = new Text(productName);

            HBox cost = new HBox();
            Text quantity = new Text(String.format("%dx",soldQuantity));
            Text price = new Text(String.format("$%.2f", productPrice));
            Region spacerCost = new Region();
            HBox.setHgrow(spacerCost, Priority.ALWAYS);
            spacerCost.setMinWidth(Region.USE_PREF_SIZE);
            cost.getChildren().addAll(quantity, spacerCost, price);

            HBox sum = new HBox();
            Text sumText = new Text("sum*n");
            Text totalPrice = new Text(String.format("$%.2f\r\n", productPrice * soldQuantity));
            Region spacerSum = new Region();
            HBox.setHgrow(spacerSum, Priority.ALWAYS);
            spacerSum.setMinWidth(Region.USE_PREF_SIZE);
            sum.getChildren().addAll(sumText, spacerSum, totalPrice);

            this.invoiceVBox.getChildren().addAll(name, cost, sum);
        }



    }


    private void buildInvoiceFooter(){


        double totalValue = 12312.0345;
        double taxBase = 0.2;
        //prob order number
        int numberInvoice = 1231;

        //total
        HBox total = new HBox();
        total.setStyle("-fx-font-size: 16px;-fx-text-alignment: right;");
        Text totalTitle = new Text("Total");
        totalTitle.setStyle(" -fx-font-weight: bold; ");
        Text totalValueText = new Text(String.format("$%.2f\r\n", totalValue));
        totalValueText.setStyle(" -fx-font-weight: bold; ");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);
        total.getChildren().addAll(totalTitle, spacer, totalValueText);

        //tax
        HBox tax = new HBox();
        total.setStyle(" -fx-text-alignment: right;");
        Text taxTitle = new Text("Tax");
        Text taxValue = new Text(String.format("$%.2f", totalValue*( 1 - taxBase)));
        Region spacerTax = new Region();
        HBox.setHgrow(spacerTax, Priority.ALWAYS);
        spacerTax.setMinWidth(Region.USE_PREF_SIZE);
        tax.getChildren().addAll(taxTitle, spacerTax, taxValue);

        //serial number
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("00000000\r\n");
        String output = df.format(numberInvoice);
        Text invoiceNumber = new Text(output);

        //date time
        LocalTime localTime = LocalTime.now();
        LocalDate dateTime = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy ", Locale.ENGLISH);
        HBox date = new HBox();
        date.setStyle(" -fx-text-alignment: right;");
        Text dateText = new Text(dateTime.format(dateFormat));
        Text timeText = new Text(localTime.format(timeFormatter));
        Region spacerDate = new Region();
        HBox.setHgrow(spacerDate, Priority.ALWAYS);
        spacerTax.setMinWidth(Region.USE_PREF_SIZE);
        date.getChildren().addAll(dateText, spacerDate, timeText);

        //bottom text
        HBox invoiceEnd = new HBox();
        invoiceEnd.setStyle(" -fx-text-alignment: right;");
        Text endTextStart = new Text("BG");
        Text endTextEnd = new Text("FISCAL RECEIPT");
        Region spacerEnd = new Region();
        HBox.setHgrow(spacerEnd, Priority.ALWAYS);
        spacerEnd.setMinWidth(Region.USE_PREF_SIZE);
        invoiceEnd.getChildren().addAll(endTextStart, spacerEnd, endTextEnd);

//        Text Separator = new Text("");
//        Text mainAddress = new Text(InvoiceSettings.BAR_ADDRESS.getSetting());
//        mainAddress.setStyle("-fx-wrap-text: true;");
//        Label barInfo = new Label(InvoiceSettings.BAR_FINANCIAL_INFO.getSetting());
//        barInfo.setStyle("-fx-wrap-text: true;");
//        Label operatorInfo = new Label(String.format(InvoiceSettings.OPERATOR_INFO.getSetting(),operator));
//        barInfo.setStyle("-fx-wrap-text: true; -fx-text-alignment: left;");
//        //       invoiceTitle.setStyle("-fx-translate-x: 70px; -fx-translate-y: 10px");
        this.invoiceVBox.getChildren().addAll(total, tax, invoiceNumber, date, invoiceEnd);
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




//    @FXML private void printInvoice(Node node){
//        this.printInvoice(node);
//    }
//
//
//    //error checking printing
//    boolean doPrint(Node n){
//        PrinterJob job = PrinterJob.createPrinterJob();
//        if (job == null){
//            return false; //no printers
//        }
//        //printing dialog, null is owner
//        if (!job.showPageSetupDialog(null)){
//            return false; //not sure false or true
//        }
//        if (!job.printPage(n)){
//            return false; //return false if error occurs when printing page
//        }
//        return job.endJob(); //return final status
//    }
//
//
//    /*
//     *datecs dpp -250
//     * Max width 48mm - 384 dots per line
//     * 32 chars per line
//     */
//
//    //Printer class equals to printer in menu - print XPS docs PDF docs
//    private void PPP() {
//        PrinterJob job = PrinterJob.createPrinterJob();
//        Printer printer = Printer.getDefaultPrinter();
//        ObservableSet<Printer> allPrinters = Printer.getAllPrinters();
//
//        //Printer attributes have only getters and setters
//
//        //JobSettings - current configuration of the PrinterJob - set and get (pages, page type and so on)
//
//        //Attributes
//
//        PrinterAttributes printerAttributes = printer.getPrinterAttributes();
//        Set<Paper> defaultPaper = printerAttributes.getSupportedPapers();
//        Paper c = Paper.C;
//        //create own layout
//
//        PageLayout pl = printer.createPageLayout(
//                Paper.C,
//                PageOrientation.LANDSCAPE,
//                Printer.MarginType.EQUAL_OPPOSITES); // margins on the page
//
//        // page x -width y-height getPrintableWidth() getPrintableHeight()
//        //example
//        JobSettings settings = job.getJobSettings();
//        PageLayout p1 = settings.getPageLayout();
//        double pageWidth = p1.getPrintableWidth();
//        double pageHeight = p1.getPrintableHeight();
//        double radius = ((pageWidth < pageHeight) ? pageWidth : pageHeight) / 2.0;
//        Circle c1 = new Circle(pageWidth/2, pageHeight/2, radius, Color.RED);
//        //Wi;; draw largest circle that can fit on the paper;
//
//        // Monitoring job status
//        PrinterJob.JobStatus printing = PrinterJob.JobStatus.PRINTING;  //status on current printer job
//
//    }

}
