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
    @FXML
    private DataFields dataFieldsController;
    @FXML
    private RectVisualization rectVisualizationController;
    ////

    // Init
    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;

        this.dataFieldsController.init(ctrl);
        this.processTableController.init(ctrl);
        this.actionButtonsController.init(ctrl, processTableController.getTable());
        this.rectVisualizationController.init(ctrl,processTableController);
        this.sourceMenuController.init(ctrl,dataFieldsController);
        this.algoComboController.init(ctrl,dataFieldsController);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // receive updates
    public void updateDisplay(MemoryEvent memEvent) {
        this.processTableController.update(memEvent);
        this.dataFieldsController.update(memEvent);
        this.rectVisualizationController.update(memEvent);
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
