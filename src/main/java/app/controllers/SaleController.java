package app.controllers;

import app.cores.StageManager;
import app.dtos.OrderDto;
import app.entities.BarTable;
import app.entities.Role;
import app.entities.User;
import app.enums.ViewPath;
import app.services.api.BarTableService;
import app.services.api.OrderService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class SaleController implements FxmlController {

    private final Image ORDERS_BUTTON_INACTIVE_IMAGE = new Image("static_data/images/ordersButton.png");
    private final Image ORDERS_BUTTON_ACTIVE_IMAGE = new Image("static_data/images/ordersButtonActive.png");
    private final Image TABLES_BUTTON_INACTIVE_IMAGE = new Image("static_data/images/tableButton.png");
    private final Image TABLES_BUTTON_ACTIVE_IMAGE = new Image("static_data/images/tableButtonActive.png");

    @FXML
    public Button logoutButton;
    @FXML
    public Button managerButton;
    @FXML
    public ImageView tablesButtonImage;
    @FXML
    public ImageView ordersButtonImage;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label currentUserLabel;
    @FXML
    private GridPane gridPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label selectedTableNumber;
    @FXML
    private ImageView managerButtonImage;

    private StageManager stageManager;
    private User currentUser;
    private List<BarTable> barTableList;
    private OrderService orderService;
    private ToggleGroup toggleGroup;
    private OrderDto orderDto;

    @Autowired
    @Lazy
    public SaleController(StageManager stageManager, BarTableService barTableService, OrderService orderService) {
        this.stageManager = stageManager;
        this.barTableList = barTableService.getAllBarTables();
        this.orderService = orderService;
    }

    @Override
    public void initialize() {
        //clock
        this.timeInfo();

        //gets UserName next to clock
        this.currentUser = this.stageManager.getUser();

        //User Init
        initUserDev();
        if (this.currentUser != null) {
            this.currentUserLabel.setText(this.currentUser.getName());

            //Hide managerButton if user is not admin
            if (!"admin".equalsIgnoreCase(this.currentUser.getRole().getRole())) {
                this.managerButton.setDisable(true);
                this.managerButtonImage.setOpacity(0);
            }
        }

        //making parent of GridPane transparent
        this.scrollPane.setStyle("-fx-background: transparent;" +
                "-fx-background-color: transparent;");

        //ToggleButtons act like RadioButtons
        this.toggleGroup = new ToggleGroup();
        this.toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
             @Override
             public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == null) {
                    toggleGroup.selectToggle(oldValue);
                }
             }
         });

        //filling GridPane with current tables
        fillTablesGrid();

        //Controller starts with view of Tables
        tablesButtonHandler();
    }

    private void timeInfo(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.ENGLISH);
            this.currentTimeLabel.setText(localDateTime.format(timeFormatter));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void logout(){
        this.stageManager.switchScene(ViewPath.LOGIN);
    }

    @FXML
    private void managerButtonHandler() {
        this.stageManager.switchScene(ViewPath.MANAGER);
    }

    @FXML
    private void ordersButtonHandler() {
        //change background of buttons
        this.tablesButtonImage.setImage(TABLES_BUTTON_INACTIVE_IMAGE);
        this.ordersButtonImage.setImage(ORDERS_BUTTON_ACTIVE_IMAGE);

        //Get Order
        if (this.toggleGroup.getSelectedToggle() == null) {
            //Shows message if table is not selected
            Label label = new Label("SELECT A TABLE");
            label.setStyle("-fx-font-size: 40px;" +
                            "-fx-alignment: center;");
            label.setPrefSize(this.scrollPane.getPrefWidth() - 10,
                    this.scrollPane.getPrefHeight() - 10);
            this.scrollPane.setContent(label);
        }
        else {
            Long tableId = (Long) this.toggleGroup.getSelectedToggle().getUserData();
            this.orderDto = this.orderService.findOpenOrderByTable(tableId);

        }
    }

    @FXML
    private void tablesButtonHandler() {
        //change background of buttons
        this.tablesButtonImage.setImage(TABLES_BUTTON_ACTIVE_IMAGE);
        this.ordersButtonImage.setImage(ORDERS_BUTTON_INACTIVE_IMAGE);

        this.scrollPane.setContent(this.gridPane);
    }

    private ToggleButton createTableToggleButton(BarTable table) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setText(String.valueOf(table.getNumber()));
        toggleButton.setUserData(table.getId());
        toggleButton.setId("tableToggleButton");

        toggleButton.setOnAction(e -> {
            this.selectedTableNumber.setText(toggleButton.getText());
        });

        toggleButton.setToggleGroup(this.toggleGroup);

        return toggleButton;
    }

    private void fillTablesGrid() {
        this.gridPane = new GridPane();
        this.gridPane.setStyle("-fx-background-color: transparent;");
        this.gridPane.setHgap(10.0);
        this.gridPane.setVgap(10.0);

        ColumnConstraints columnConstraints = new ColumnConstraints(150);
        RowConstraints rowConstraints = new RowConstraints(120);

        for (int i = 0; i < 10; i++) {
            this.gridPane.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < 3; i++) {
            this.gridPane.getColumnConstraints().add(columnConstraints);
            for (int j = 0; j < 10; j++) {

                //TODO fix BarTable
                ToggleButton toggleButton = createTableToggleButton(new BarTable());
                GridPane.setHalignment(toggleButton, HPos.CENTER);
                this.gridPane.add(toggleButton, i, j);
            }
        }
    }

    //TODO Remove before building
    private void initUserDev() {
        this.currentUser = new User();
        this.currentUser.setName("Pesho");
        Role role = new Role();
        role.setRole("dasda");
        this.currentUser.setRole(role);
    }
}
