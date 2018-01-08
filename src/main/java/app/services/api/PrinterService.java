package app.services.api;

import javafx.print.Printer;
import javafx.scene.Node;

public interface PrinterService {
    Printer getDefaultPrinters();

    String printNode(Node node);
}
