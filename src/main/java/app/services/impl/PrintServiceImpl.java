package app.services.impl;

import app.services.api.PrinterService;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import org.springframework.stereotype.Service;

@Service
public class PrintServiceImpl implements PrinterService {

    @Override
    public Printer getDefaultPrinters(){
        return Printer.getDefaultPrinter();
    }

    @Override
    public String printNode(Node node){
        String errorMessage ="";
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            // Print the node
            boolean printed = job.printPage(node);
            if (printed)
            {
                // End the printer job
                job.endJob();
            } else {
                // Write Error Message
                errorMessage = "Printing failed.";
            }
        } else {
           errorMessage="Could not create a printer job.";
        }
        return errorMessage;
    }



}
