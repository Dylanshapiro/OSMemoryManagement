package view;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import model.Algos.Algo;

import javafx.scene.control.TableView;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

public class Prefs implements Initializable {

    Controller ctrl;

    @FXML
    TableView<String> tableView;



    public void dynamicInit(Controller ctrl){
        this.ctrl = ctrl;
        this.tableView.getItems().addAll(ctrl.getRemoteNodes());
    }

    public void setController(Controller ctrl){
        this.ctrl = ctrl;
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
