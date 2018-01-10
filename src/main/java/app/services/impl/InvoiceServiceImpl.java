package app.services.impl;

import app.controllers.InvoiceController;
import app.cores.StageManager;
import app.entities.Product;
import app.enums.ViewElementPath;
import app.services.api.InvoiceService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class InvoiceServiceImpl implements InvoiceService {


    private StageManager stageManager;
    private InvoiceController invoiceController;
    private Stage invoiceStage;

    @Autowired
    @Lazy
    public InvoiceServiceImpl(StageManager stageManager, InvoiceController invoiceController) {
        this.stageManager = stageManager;
        this.invoiceController = invoiceController;
    }

    @Override
    public void makeInvoice(String operator, Map<Product, Integer> products, Long id){

        this.invoiceStage = new Stage();
        Parent invoiceParent = this.stageManager.getPane(ViewElementPath.INVOICE);
        this.invoiceStage.initStyle(StageStyle.UNDECORATED);

        //pop up window must be closed to continue interaction with the program
        this.invoiceStage.initModality(Modality.APPLICATION_MODAL);
        this.invoiceStage.setTitle("Invoice");

        //set scene
        Scene invoiceScene = new Scene(invoiceParent);
        this.invoiceStage.setScene(invoiceScene);

        this.invoiceController.buildInvoice(this.invoiceStage, operator, products, id);

        this.invoiceStage.showAndWait();
    }

    @Override
    public boolean isInvoicePaid(){
        return this.invoiceController.isInvoicePaid();
    }
}
