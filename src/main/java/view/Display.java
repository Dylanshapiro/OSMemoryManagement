package view;


import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Algos.Algo;
import model.MemoryManager;
import model.MemoryManager.MemoryEvent;
import model.process.Process;

import javax.management.InstanceNotFoundException;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.OptionalLong;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    private TableView<ProcessEntry> procTable;

    @FXML
    private Button killProcessButton;

    @FXML
    private Button toggleSimButton;

    @FXML
    private AnchorPane memoryViewPane;

    @FXML
    private Rectangle memoryRect;

    private boolean simEnabled;

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu sourceMenu;

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


    // Init
    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;
    }

    public void initTable() {

        TableColumn<ProcessEntry, String> name = new TableColumn<>("Name");
        name.setPrefWidth(75);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProcessEntry, Integer> id = new TableColumn<>("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ProcessEntry, Long> startTime = new TableColumn<>("StartTime");
        startTime.setPrefWidth(75);
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<ProcessEntry, Long> base = new TableColumn<>("Base");
        base.setPrefWidth(75);
        base.setCellValueFactory(new PropertyValueFactory<>("base"));

        TableColumn<ProcessEntry, Long> size = new TableColumn<>("Size");
        size.setPrefWidth(75);
        size.setCellValueFactory(new PropertyValueFactory<>("size"));

        procTable.getColumns().addAll(name, id, startTime, base, size);

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

        // Sets up names for Combo Box
        algoCombo.setCellFactory(listView -> new Display.SimpleTableObjectListCell());
        algoCombo.setButtonCell(new Display.SimpleTableObjectListCell());
        algoCombo.getSelectionModel().selectFirst();

        algoCombo.setOnAction(this::setAlgo);


        this.initTable();
        this.loadSourceMenu();
        this.initInfoFields();
    }

    private void initInfoFields() {
        this.updateSourceText(sourceMenu.getItems().get(0).getText());
        this.updateProcNumText(procTable.getItems().size());
        this.updateAlgoText(algoCombo.getItems().get(0).getName());
        this.updateUsedMemoryText("0");
        this.updateTotalMemoryText(String.valueOf(this.ctrl.getMemSize()));
    }

    // Source Menu
    private void changedSource(ActionEvent actionEvent) {
        MenuItem newSource = (MenuItem) actionEvent.getSource();

        String id = "0";

        for (MenuItem item : sourceMenu.getItems()) {
            CheckMenuItem checkItem = (CheckMenuItem) item;
            if (item == newSource) {
                checkItem.setSelected(true);
                id = checkItem.getId();
                this.updateSourceText(checkItem.getText());
            } else {
                checkItem.setSelected(false);
            }
        }


        try {
            this.ctrl.setSource(id);
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadSourceMenu() {
        sourceMenu.getItems().addAll(this.ctrl.getSourceList()
                .stream().map(node -> {
                    String id = String.valueOf(node.getId());

                    CheckMenuItem menuItem = new CheckMenuItem(node.toString());
                    menuItem.setId(id);
                    menuItem.setOnAction(this::changedSource);

                    if (node.getId() == 0) {
                        updateSourceText( menuItem.getText());
                        menuItem.setSelected(true);
                    }

                    return menuItem;
                }).collect(Collectors.toList()));
    }

    private void enableSourceMenu(boolean enabled) {
        sourceMenu.getItems().forEach(item -> {
            item.setDisable(!enabled);
        });
    }

    // Info fields
    private void updateProcNumText(int num){
        this.curProcNumText.setText(toString().valueOf(num));
    }

    private void updateSourceText(String source){
        this.curSourceText.setText(source);
    }

    private void updateAlgoText(String algo){
        this.curAlgoText.setText(algo);
    }

    private void updateUsedMemoryText(String used){
        this.curMemUsedText.setText(used);
    }

    private void updateTotalMemoryText(String total){
        this.totalMemoryText.setText(total);
    }

    private OptionalLong calcUsedMem(MemoryEvent event){

        return  event.getProcesses().stream()
                .mapToLong(proc -> proc.getSize())
                .reduce((acc, cur) -> {
                    return acc + acc;
                });
    }

    // Proc List
    private void updateProcList(MemoryEvent event) {
        // adapt process to a form that TableView needs

        ObservableList<ProcessEntry> processEntries =
                FXCollections.observableArrayList(event.getProcesses().stream().map(proc -> {
                    return new ProcessEntry(proc.getName(),
                            proc.getProcId(),
                            proc.getStartTime(),
                            proc.getBaseAddress().get(),
                            proc.getSize());
                }).collect(Collectors.toList()));

        this.procTable.setItems(processEntries);
    }

    // receive updates
    public void updateDisplay(MemoryManager.MemoryEvent memEvent) {
        updateProcList(memEvent);
        this.updateProcNumText(memEvent.getProcesses().size());
        this.updateUsedMemoryText("" + calcUsedMem(memEvent).getAsLong());
        this.deleteChunk();

        for(Process p:memEvent.getProcesses()){
            double size = (double) p.getSize() / (double) memEvent.getMemSize();
            double baseAddress = (double) p.getBaseAddress().get() / (double) memEvent.getMemSize();

            Rectangle chunk = fillChunk(size, baseAddress);

            if (p.equals(memEvent.getLastChanged())) {
                chunk.setFill(Color.BISQUE);

            } else {
                chunk.setFill(Color.BLACK);
            }

                chunk.addEventFilter(MouseEvent.MOUSE_CLICKED, e2 ->{

                    if(e2.getButton()== MouseButton.PRIMARY)
                    {
                     //TODO Select process that this chunk represents in the procTable

                    } });

                memoryViewPane.getChildren().add(chunk);
        }

    }

    // Input Events
    public void killProc(ActionEvent event) {
        final int pid = procTable.getSelectionModel().getSelectedItem().getId();
        this.ctrl.killProc(pid);

    }

    public void setAlgo(ActionEvent event) {
        Algo selectedItem = algoCombo.getSelectionModel().getSelectedItem();
        this.ctrl.setAlgo(selectedItem);
        this.updateAlgoText(selectedItem.getName());
    }

    private void toggleSim(ActionEvent actionEvent) {
        Button button =  (Button)actionEvent.getSource();
        if (this.simEnabled) {
            button.setText("Run Sim! =)");
            this.simEnabled = false;
            this.ctrl.stopSim();
            enableSourceMenu(true);
        } else {
            button.setText("Stop Sim! =0");
            this.simEnabled = true;
            this.ctrl.startSim();
            enableSourceMenu(false);
        }
    }

    @FXML
    public void addProc(ActionEvent event) {
        this.ctrl.addProc();
    }

    public Rectangle fillChunk(double processSize, double processAddress) {

        Rectangle chunk = new Rectangle();

        chunk.setX(8 + (processAddress * memoryRect.getWidth()));
        chunk.setY(101);
        chunk.setHeight(memoryRect.getHeight());
        chunk.setWidth( processSize * memoryRect.getWidth());

        return chunk;
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

    // Navigation stuff
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
