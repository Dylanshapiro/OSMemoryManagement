package Controller;

import Model.*;
import Model.Algos.*;
import Model.Process;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class Controller implements MemoryObserver, Initializable {

    private ProcessSource source;
    private MemoryManager manager;

    @FXML
    private ComboBox<Algo> algoCombo;

    @FXML
    private Button generateButton;

    @FXML
    private ListView<Process> statusField;

    @FXML
    private Button killProcessButton;


    public Controller() {

        this.source = new SimSource(0);
        this.manager = MemoryManager.getInstance();
        this.manager.addObserver(this);


    }

    public void update(MemoryObservable obs, MemoryManager.MemoryEvent MemEvent) {


        //update status field
        statusField.getItems().setAll(MemEvent.getProcesses());

    }

    public void killProc(ActionEvent event) {

        manager.deallocate((statusField.getSelectionModel().getSelectedItem()));
    }

    public void setAlgo(ActionEvent event) {
        Algo a = (Algo) event.getSource();
        manager.setAlgo(a);
    }

    @FXML
    public void addProc(ActionEvent event) {
        manager.allocate(source.generateProcess());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Generate Process Button
        generateButton.setOnAction(this::addProc);
        //Kill Process Button
        killProcessButton.setOnAction(this::killProc);

        //Algo Combo box
        algoCombo.getItems().addAll(new FirstFitAlgo(manager.getMemSize()),
                new BestFitAlgo(manager.getMemSize()),
                new WorstFitAlgo(manager.getMemSize()),
                new NextFitAlgo(manager.getMemSize()),
                new BuddyAlgo(manager.getMemSize()));
        //Sets up names for Combo Box
        algoCombo.setCellFactory(listView -> new SimpleTableObjectListCell());
        algoCombo.setButtonCell(new SimpleTableObjectListCell());
        algoCombo.getSelectionModel().selectFirst();
       //stack exchange told me to do this, supposed to make combobox work
        EventHandler<ActionEvent> handler = algoCombo.getOnAction();
        algoCombo.setOnAction(null);
        algoCombo.setItems(algoCombo.getItems());
        algoCombo.setOnAction(handler);

    }

    /**
     * This just sets the names for the Algorithms.
     */
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