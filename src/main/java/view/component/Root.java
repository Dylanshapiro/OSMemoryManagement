package view.component;


import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import model.MemoryManager.MemoryEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

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

    @FXML
    private MiniTerm miniTermController;
    ////

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.actionButtonsController.init(processTableController.getTable());
        this.rectVisualizationController.init(processTableController);
        this.sourceMenuController.init(dataFieldsController);
        this.algoComboController.init(dataFieldsController);
    }

    // receive updates
    public void updateDisplay(MemoryEvent memEvent) {
        this.processTableController.update(memEvent);
        this.dataFieldsController.update(memEvent);
        this.miniTermController.update(memEvent);
        this.rectVisualizationController.update(memEvent);
    }

    // Navigation stuff
    @FXML
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
