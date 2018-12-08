package view.component;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SourceMenu implements Initializable {

    // From Fxml
    @FXML
    private Menu sourceMenu;

    // From init

    private DataFields dataFieldsController;
    private Controller ctrl;

    // init
    public void init(DataFields dataFieldsController){

        this.loadSourceMenu();
        this.dataFieldsController= dataFieldsController;

        this.dataFieldsController.setSourceText(this.sourceMenu.getItems().get(0).getText());

        CheckMenuItem  first =(CheckMenuItem) this.sourceMenu.getItems().get(0);
        first.setSelected(true);
    }

    private void loadSourceMenu() {
        sourceMenu.getItems().addAll(this.ctrl.getSourceList()
                .stream().map(node -> {
                    String id = String.valueOf(node.getId());

                    CheckMenuItem menuItem = new CheckMenuItem(node.toString());
                    menuItem.setId(id);
                    menuItem.setOnAction(this::changedSource);

                    if (node.getId() == 0) {
                        this.dataFieldsController.setSourceText(menuItem.getText());
                        menuItem.setSelected(true);
                    }

                    return menuItem;
                }).collect(Collectors.toList()));
    }

    private void changedSource(ActionEvent actionEvent) {
        MenuItem newSource = (MenuItem) actionEvent.getSource();

        String id = "0";

        for (MenuItem item : sourceMenu.getItems()) {
            CheckMenuItem checkItem = (CheckMenuItem) item;
            if (item == newSource) {
                checkItem.setSelected(true);
                id = checkItem.getId();
                this.dataFieldsController.setSourceText(checkItem.getText());
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

    private void enableSourceMenu(boolean enabled) {
        sourceMenu.getItems().forEach(item -> {
            item.setDisable(!enabled);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
