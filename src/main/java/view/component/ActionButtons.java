package view.component;

import controller.Controller;
import driver.ComponentFactory.CTRL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.media.AudioClip;
import view.ProcessEntry;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class ActionButtons implements Initializable {

    // From fxml

    // From init
    @CTRL
    private Controller ctrl;
    private TableView<ProcessEntry> procTable;

    private boolean simEnabled;
    private AudioClip buttonSound;

    @FXML
    private Button toggleSimButton;

    // init
    public void init(Node table) {
        this.procTable = (TableView<ProcessEntry>) table;
        buttonSound = new AudioClip(Paths.get("./src/main/resources/oof.mp3").toUri().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Input Events
    @FXML
    public void killProc(ActionEvent event) {
        if(! procTable.getSelectionModel().isEmpty()){

            Integer id = procTable.getSelectionModel().getSelectedItem().getId();
            this.ctrl.killProc(id);
            buttonSound.play();
        }
    }

    @FXML
    private void toggleSim(ActionEvent actionEvent) {
        if (this.simEnabled) {
            this.stopSim();
        } else {
            this.startSim();
        }
    }

    @FXML
    private void startSim(){
        this.toggleSimButton.setText("Stop! =0");
        this.simEnabled = true;
        this.ctrl.startSim();
    }

    @FXML
    private void stopSim(){
        this.toggleSimButton.setText("Start! =)");
        this.simEnabled = false;
        this.ctrl.stopSim();
    }

    @FXML
    public void addProc(ActionEvent event) {
        this.ctrl.addProc();
    }

    @FXML
    public void reset(ActionEvent event) {
        this.stopSim();
        this.ctrl.resetSim();
    }
}
