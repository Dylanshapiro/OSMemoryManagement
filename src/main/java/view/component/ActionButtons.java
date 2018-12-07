package view.component;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import model.Algos.Algo;
import view.ProcessEntry;

import java.net.URL;
import java.util.ResourceBundle;

public class ActionButtons implements Initializable {

    // From fxml

    // From init
    private Controller ctrl;
    private TableView<ProcessEntry> procTable;

    private boolean simEnabled;

    // init
    public void init(Controller ctrl,Node table) {
        this.ctrl = ctrl;
        this.procTable = (TableView<ProcessEntry>) table;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Input Events
    @FXML
    public void killProc(ActionEvent event) {
        final int pid = procTable.getSelectionModel().getSelectedItem().getId();
        this.ctrl.killProc(pid);
    }

    @FXML
    private void toggleSim(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        if (this.simEnabled) {
            button.setText("Run Sim! =)");
            this.simEnabled = false;
            this.ctrl.stopSim();
            // enableSourceMenu(true);
        } else {
            button.setText("Stop Sim! =0");
            this.simEnabled = true;
            this.ctrl.startSim();
            //  enableSourceMenu(false);
        }
    }

    @FXML
    public void addProc(ActionEvent event) {
        this.ctrl.addProc();
    }

    @FXML
    public void reset(ActionEvent event){
        this.ctrl.resetSim();
    }

}
