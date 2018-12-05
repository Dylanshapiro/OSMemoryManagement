package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableView;
import model.Algos.Algo;

import java.net.URL;
import java.util.ResourceBundle;

public class AlgoCombo implements Initializable {


    // From fxml
    @FXML
    private ComboBox<Algo> algoCombo;

    // From init
    private Controller ctrl;
    private Label currentAlgoLabel;

    // init
    public void init(Controller ctrl, Node curAlgoLabel){
        this.ctrl = ctrl;

        // Algo Combo box
        algoCombo.getItems().addAll(this.ctrl.getAlgoList());

        // Sets up names for Combo Box
        algoCombo.setCellFactory(listView -> new SimpleTableObjectListCell());
        algoCombo.setButtonCell(new SimpleTableObjectListCell());
        algoCombo.getSelectionModel().selectFirst();

        algoCombo.setOnAction(this::setAlgo);

        this.currentAlgoLabel = (Label) curAlgoLabel;
        this.currentAlgoLabel.setText(algoCombo.getItems().get(0).getName());
    }

    public void setAlgo(ActionEvent event) {
        Algo selectedItem = algoCombo.getSelectionModel().getSelectedItem();
        this.ctrl.setAlgo(selectedItem);
        this.currentAlgoLabel.setText(selectedItem.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
