package view.component;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import model.MemoryManager.MemoryEvent;
import model.MemoryManager.Op;
import model.process.Process;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MiniTerm implements Initializable {

    @FXML
    private TextArea miniTerm;

    private Controller ctrl;

    private final int nameLenSpace = 15;
    private final int titleLenSpace = 7;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        miniTerm.setWrapText(true);
    }

    public void update(MemoryEvent event) {

        Optional<Process> lastChanged = Optional.ofNullable(event.getLastChanged());
        Op opType = event.getOpType();

        if ( opType ==  Op.ADD || opType == Op.DELETE ){
            final String procName = event.getLastChanged().getName();
            final String nameOut =  (procName.length() > nameLenSpace) ?
                                    procName.substring(0,nameLenSpace - 3 ).concat("...") : procName;

            String toAppend = String.format( "%-7s: %-15s\n" ,
                    opType,
                    nameOut );

            miniTerm.appendText(toAppend);
        }

        printStatus(event);
    }

    private void printStatus(MemoryEvent event){
        String op = event.getOpType().name();

        event.getStatusMessage().ifPresent(status ->  {
            miniTerm.appendText(String.format("%-7s: %s%n", op, status));
        });
    }

}
