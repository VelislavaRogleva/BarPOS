package app.services.impl;

import app.services.api.PrinterService;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

@Service
public class PrinterServiceImpl implements PrinterService {

    private static final String PRINTING_FAILED_ERROR = "Printing failed.";
    private static final String PRINTER_JOB_CREATION_ERROR = "Could not create a printer job.";

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
                errorMessage = PRINTING_FAILED_ERROR;
            }
        } else {
           errorMessage= PRINTER_JOB_CREATION_ERROR;
        }
        return errorMessage;
    }



}
