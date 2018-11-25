package View;

import Controller.Controller;
import Model.Algos.Algo;
import Model.MemoryManager;
import Model.Process;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class Display implements Initializable {
    @FXML
    private Controller ctrl;

    @FXML
    private ComboBox<Algo> algoCombo;

    @FXML
    private Button generateButton;

    @FXML
    private ListView<Process> statusField;

    @FXML
    private Button killProcessButton;

    // Init
    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Generate Process Button
        generateButton.setOnAction(this::addProc);
        // Kill Process Button
        killProcessButton.setOnAction(this::killProc);

        // Algo Combo box
        algoCombo.getItems().addAll(this.ctrl.getAlgoList());

        // Sets up names for Combo Box
        algoCombo.setCellFactory(listView -> new Display.SimpleTableObjectListCell());
        algoCombo.setButtonCell(new Display.SimpleTableObjectListCell());
        algoCombo.getSelectionModel().selectFirst();

        // stack exchange told me to do this, supposed to make combobox work
        EventHandler<ActionEvent> handler = algoCombo.getOnAction();
        algoCombo.setOnAction(null);
        algoCombo.setItems(algoCombo.getItems());
        algoCombo.setOnAction(handler);

    }

    // receive updates
    public void updateDisplay(MemoryManager.MemoryEvent memEvent) {
        statusField.getItems().setAll(memEvent.getProcesses());
    }

    // Input Events
    public void killProc(ActionEvent event) {
        this.ctrl.killProc(statusField.getSelectionModel().getSelectedItem());
    }

    public void setAlgo(ActionEvent event) {
        Algo a = (Algo) event.getSource();
        this.ctrl.setAlgo(a);
    }

    @FXML
    public void addProc(ActionEvent event) {
        this.ctrl.addProc();
    }

    // This just sets the names for the Algorithms.
    private static class SimpleTableObjectListCell extends ListCell<Algo> {

        @Override
        public void updateItem(Algo item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(item.getName());//return String, actual name of material
            } else {
                setText(null);
            }
        }
    }
}
