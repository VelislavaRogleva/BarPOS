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
    private static final String[] ORDER_STATUS = {"closed", "open", "cancelled"};
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
    private UserService userService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageSaleController(StageManager stageManager, StatisticService statisticService, UserService userService) {
        super(stageManager);
        this.statisticService = statisticService;
        this.userService = userService;
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
}
