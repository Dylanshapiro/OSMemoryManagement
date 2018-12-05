package view;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import model.Algos.Algo;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SourceMenu implements Initializable {

    @FXML
    private Menu sourceMenu;

    @FXML
    private Label currentSourceLabel;

    @FXML
    private Controller ctrl;

    public void init(Controller ctrl, Node curSourceLabel){
        this.ctrl = ctrl;
        this.loadSourceMenu();

        this.currentSourceLabel = (Label) curSourceLabel;

        this.currentSourceLabel.setText(this.sourceMenu.getItems().get(0).getText());

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
                        this.currentSourceLabel.setText(menuItem.getText());
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
                this.currentSourceLabel.setText(checkItem.getText());
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
