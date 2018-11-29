package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Algos.Algo;
import model.MemoryManager;
import model.Process;
import controller.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Display implements Initializable {

    @FXML
    private VBox rootBox;


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

    @FXML
    private Button toggleSimButton;

    @FXML
    private AnchorPane memoryViewPane;

    @FXML
    private Rectangle memoryRect;


    @FXML
    private MenuItem launchPrefs;

    private boolean simEnabled;

    // Init
    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Buttons
        // Generate Process Button
        generateButton.setOnAction(this::addProc);
        // Kill Process Button
        killProcessButton.setOnAction(this::killProc);
        // Toggle Sim Button
        toggleSimButton.setOnAction(this::toggleSim);

        // Algo Combo box
        algoCombo.getItems().addAll(this.ctrl.getAlgoList());

        // launch prefs window
        launchPrefs.setOnAction(this::launchPrefsWindow);
        // Sets up names for Combo Box
        algoCombo.setCellFactory(listView -> new Display.SimpleTableObjectListCell());
        algoCombo.setButtonCell(new Display.SimpleTableObjectListCell());
        algoCombo.getSelectionModel().selectFirst();

        algoCombo.setOnAction(this::setAlgo);
    }

    // receive updates
    public void updateDisplay(MemoryManager.MemoryEvent memEvent) {
        statusField.getItems().setAll(memEvent.getProcesses());
        this.deleteChunk();
        for(Process p:memEvent.getProcesses()){
            double size = (double) p.getSize() / (double) memEvent.getMemSize();
            double baseAddress = (double) p.getBaseAddress().get() / (double) memEvent.getMemSize();
            fillChunk(size,baseAddress);
        }

    }

    // Input Events
    public void killProc(ActionEvent event) {
        this.ctrl.killProc(statusField.getSelectionModel().getSelectedItem());
    }

    public void setAlgo(ActionEvent event) {
        this.ctrl.setAlgo(algoCombo.getSelectionModel().getSelectedItem());
    }

    private void toggleSim(ActionEvent actionEvent) {
        Button button =  (Button)actionEvent.getSource();
        if (this.simEnabled) {
            button.setText("Run Sim! =)");
            this.simEnabled = false;
            this.ctrl.stopSim();
        } else {
            button.setText("Stop Sim! =0");
            this.simEnabled = true;
            this.ctrl.startSim();
        }
    }

    @FXML
    public void addProc(ActionEvent event) {
        this.ctrl.addProc();
    }

    public void fillChunk(double processSize, double processAddress) {
        Rectangle chunk = new Rectangle();

        chunk.setX(8 + (processAddress * memoryRect.getWidth()));
        chunk.setY(101);
        chunk.setHeight(memoryRect.getHeight());
        chunk.setWidth( processSize * memoryRect.getWidth());

        memoryViewPane.getChildren().add(chunk);


    }

    public void deleteChunk(){
        memoryViewPane.getChildren().remove(2, memoryViewPane.getChildren().size());
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

    private void launchPrefsWindow(ActionEvent actionEvent) {
        Parent prefsWindow;
        try {
            FXMLLoader loader = new FXMLLoader();
            prefsWindow = (AnchorPane) loader.load(getClass().getResource("../xml/prefs.fxml"));

            Scene prefScene = new Scene(prefsWindow);
            Stage curStage = (Stage) rootBox.getScene().getWindow();

            curStage.setResizable(false);
            curStage.setScene(prefScene);

            Prefs prefController = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
