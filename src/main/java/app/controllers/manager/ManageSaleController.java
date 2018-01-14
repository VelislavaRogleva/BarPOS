package app.controllers.manager;

import app.cores.StageManager;
import app.dtos.StatisticProductDto;
import app.services.api.StatisticService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class ManageSaleController extends BaseManageController {

    private static final String SOLD_COLUMN_VALUE_FIELD_NAME = "sold";
    private static final String[] AVAILABLE_FILTERS = {"profit", "quantity"}; //popularity filter product by quantity sold descending
    private static final String[] ORDER_STATUS = {"closed", "open", "cancelled"};
    private static final String[] RESULT_COUNT = {"5", "10", "15", "20", "25", "All"};
    private static final int COUNT_COLUMNS = 5;
    private static final Double TABLE_DEFAULT_WIDTH = 780.0;
    private static final Double TABLE_OFFSET = 5.0;
    private static final int DAYS_TO_SUBTRACT = 30;
    private static final String GENERIC_TABLE_STYLE_CLASS_NAME = "contentTable";
    private static final String NAME_COLUMN_TITLE = "name";
    private static final String NAME_COLUMN_VALUE_FIELD_NAME = "name";
    private static final String QUANTITY_COLUMN_TITLE = "quantity";
    private static final String PRICE_COLUMN_TITLE = "price";
    private static final String PRICE_COLUMN_VALUE_FIELD_NAME = "price";
    private static final String COST_COLUMN_TITLE = "cost";
    private static final String COST_COLUMN_VALUE_FIELD_TITLE = "cost";
    private static final String PROFIT_COLUMN_TITLE = "profit";
    private static final String PROFIT_COLUMN_VALUE_FIELD_NAME = "profit";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String CHART_ACCUMULATED_LEGEND = "accumulated %s";
    private static final String CHART_PROFIT_LEGEND = "profit";
    private static final int DAYS_TO_ADD = 1;
    private static final int SPLIT_ORDERS_FROM_INDEX = 0;


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
        this.genericTable.getStyleClass().addAll(GENERIC_TABLE_STYLE_CLASS_NAME);
        this.genericTable.setStyle("-fx-pref-height: 161px");
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = (TABLE_DEFAULT_WIDTH - TABLE_OFFSET)/COUNT_COLUMNS;

        TableColumn<StatisticProductDto, String> nameColumn = new TableColumn<>(NAME_COLUMN_TITLE);
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(NAME_COLUMN_VALUE_FIELD_NAME));

        //sold (quantity)
        TableColumn<StatisticProductDto, BigDecimal> quantityColumn = new TableColumn<>(QUANTITY_COLUMN_TITLE);
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
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>(SOLD_COLUMN_VALUE_FIELD_NAME));

        TableColumn<StatisticProductDto, Double> priceColumn = new TableColumn<>(PRICE_COLUMN_TITLE);
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
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(PRICE_COLUMN_VALUE_FIELD_NAME));

        TableColumn<StatisticProductDto, Double> costColumn = new TableColumn<>(COST_COLUMN_TITLE);
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
        costColumn.setCellValueFactory(new PropertyValueFactory<>(COST_COLUMN_VALUE_FIELD_TITLE));


        TableColumn<StatisticProductDto, Double> profitColumn = new TableColumn<>(PROFIT_COLUMN_TITLE);
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
        profitColumn.setCellValueFactory(new PropertyValueFactory<>(PROFIT_COLUMN_VALUE_FIELD_NAME));


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
        this.endDate.setValue(currentDate.plusDays(DAYS_TO_ADD));

        this.changeDatePickerFormat(this.startDate, DATE_FORMAT);
        this.changeDatePickerFormat(this.endDate, DATE_FORMAT);
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

        String resultFilter = this.getComboBoxValue(this.resultFilter);
        try {
           int resultsQuantity = Integer.parseInt(resultFilter);
           int ordersCount = availableOrders.size();
           if (ordersCount - resultsQuantity > 0){
               availableOrders = availableOrders.subList(SPLIT_ORDERS_FROM_INDEX, resultsQuantity);
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
        accumulated.setName(String.format(CHART_ACCUMULATED_LEGEND, title));
        for (StatisticProductDto productDto : orders) {
            String name = productDto.getName();
            Double value = title.equalsIgnoreCase(CHART_PROFIT_LEGEND) ? productDto.getProfit() : productDto.getSold().doubleValue();
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
