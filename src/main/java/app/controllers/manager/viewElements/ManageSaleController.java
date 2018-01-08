package app.controllers.manager.viewElements;

import app.controllers.manager.manager_dialogs.DeleteButtonCell;
import app.controllers.manager.manager_dialogs.EditButtonCell;
import app.cores.StageManager;
import app.entities.Product;
import app.entities.Role;
import app.entities.User;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
public class ManageSaleController extends BaseManageController {

    private static final String[] AVAILABLE_FILTERS = {"overall", "popularity", "day of Week", "category"}; //popularity filter product by quantity sold descending
    private static final String[] ORDER_STATUS = {"open", "close", "canceled"};
    private static final int OBJECT_COUNT_PROPERTIES = 2;

    @FXML private Pane chartAnchor;
    @FXML private Chart chart;
    @FXML private ComboBox orderStatus;
    @FXML private ComboBox filters;


    private UserService userService;
    ////////////////////////////////////////////////////////
    //TODO delete this!! only for dev
    private PassKeyVerificationService categoryService;
    //////////////////////////////////////////////////////////
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageSaleController(StageManager stageManager, UserService userService, PassKeyVerificationService categoryService) {
        super(stageManager);
        this.userService = userService;
        this.categoryService = categoryService;
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

}
