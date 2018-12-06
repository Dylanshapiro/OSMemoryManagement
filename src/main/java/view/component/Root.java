package view.component;


import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.MemoryManager;
import model.MemoryManager.MemoryEvent;
import model.process.Process;
import view.ProcessEntry;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Root implements Initializable {

    @FXML
    private VBox rootBox;

    @FXML
    private Controller ctrl;

    //Refactored
    @FXML
    private SourceMenu sourceMenuController;
    @FXML
    private AlgoCombo algoComboController;
    @FXML
    private ActionButtons actionButtonsController;
    @FXML
    private ProcessTable processTableController;

    ////
    @FXML
    private AnchorPane memoryViewPane;

    @FXML
    private Rectangle memoryRect;

    @FXML
    private SplitPane memorySplitPane;

    @FXML
    private MenuBar menuBar;


    // info fields
    @FXML
    private Label curSourceText;

    @FXML
    private Label curAlgoText;

    @FXML
    private Label curProcNumText;

    @FXML
    private Label curMemUsedText;

    @FXML
    private Label totalMemoryText;

    private Rectangle activeChunk;


    // Init
    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;

        this.processTableController.init(ctrl);
        this.actionButtonsController.init(ctrl,memoryRect,processTableController.getTable() );
        this.sourceMenuController.init(ctrl,curSourceText);
        this.algoComboController.init(ctrl,curAlgoText);

        this.initInfoFields();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void initInfoFields() {
        this.updateProcNumText(processTableController.getTable()
                                            .getItems().size());
        this.updateUsedMemoryText("0");
        this.updateTotalMemoryText(String.valueOf(this.ctrl.getMemSize()));
    }

    // Source Menu


    // Info fields
    private void updateProcNumText(int num){
        this.curProcNumText.setText(toString().valueOf(num));
    }

    private void updateSourceText(String source) {
        this.curSourceText.setText(source);
    }

    private void updateAlgoText(String algo) {
        this.curAlgoText.setText(algo);
    }

    private void updateUsedMemoryText(String used) {
        this.curMemUsedText.setText(used);
    }

    private void updateTotalMemoryText(String total) {
        this.totalMemoryText.setText(total);
    }

    private Long calcUsedMem(MemoryEvent event) {

        return event.getProcesses().stream()
                .mapToLong(proc -> proc.getSize())
                .reduce((acc, cur) -> {
                    return acc + acc;
                }).orElseGet( () -> new Long(0));
    }




    // receive updates
    public void updateDisplay(MemoryManager.MemoryEvent memEvent) {
        this.processTableController.update(memEvent);

        this.updateProcNumText(memEvent.getProcesses().size());
        //TODO convert this below to KB or MB
        this.updateUsedMemoryText("" + calcUsedMem(memEvent));
        this.deleteChunk();

        for (Process p : memEvent.getProcesses()) {
            double size = (double) p.getSize() / (double) memEvent.getMemSize();
            double baseAddress = (double) p.getBaseAddress().get() / (double) memEvent.getMemSize();

            Rectangle chunk = fillChunk(size, baseAddress);

            linkChunkToRow(chunk ,p); // link a chunk to process entry
            if (p.equals(memEvent.getLastChanged())) {

                setActiveChunk(chunk);
            }
            //TODO simulate mouse click to have newly added chunk already selected upon spawning.
            memoryViewPane.getChildren().add(chunk);
        }

    }

    public Rectangle fillChunk(double processSize, double processAddress) {

        Rectangle chunk = new Rectangle();
        chunk.setX(memoryRect.getLayoutX() + (processAddress * memoryRect.getWidth()));
        chunk.setY(memoryRect.getLayoutY());
        chunk.setHeight(memoryRect.getHeight());
        chunk.setWidth(processSize * memoryRect.getWidth());

        return chunk;
    }

    public void linkChunkToRow(Rectangle chunk, Process process) {

        Optional<ProcessEntry> matchedEntry = processTableController.getTable()
                .getItems()
                .stream()
                .filter(pEntry -> pEntry.getId() == process.getProcId())
                .findFirst();

        if (matchedEntry.isPresent()) {
            chunk.setOnMouseClicked(event -> {
                this.processTableController.getTable()
                        .getSelectionModel()
                        .select(matchedEntry.get());
                this.setActiveChunk(chunk);
            });

        } else {
            System.err.print("No tableview entry matches our process, "
                    + "something is wrong");
        }
    }

    public void setActiveChunk (Rectangle chunk) {
        if (activeChunk != null) {
            activeChunk.setFill(Color.BLACK);
        }

        activeChunk = chunk;
        activeChunk.setFill(Color.BISQUE);
    }

    public void deleteChunk() {
        memoryViewPane.getChildren().remove(2, memoryViewPane.getChildren().size());
    }

    // Navigation stuff
    private void launchAbout(ActionEvent actionEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(URI.create("https://github.com/Dylanshapiro/OSMemoryManagement"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
