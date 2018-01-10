package app.services.api;

import javafx.print.Printer;
import javafx.scene.Node;
import javafx.stage.Stage;

public interface PrinterService {
    Printer getDefaultPrinters();

    String printNode(Node node);
}
