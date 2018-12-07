package view.component;

import javafx.beans.property.SimpleStringProperty;
import model.process.Process;
import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.MemoryManager.MemoryEvent;
import view.ProcessEntry;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ProcessTable implements Initializable {

    // from fxml
    @FXML
    private TableView<ProcessEntry> processTable;

    // from init
    private Controller ctrl;

    public void init(Controller ctrl) {
        this.ctrl = ctrl;

        this.initTable();
    }

    private void initTable() {

        TableColumn<ProcessEntry, String> name = new TableColumn<>("Name");
        name.setPrefWidth(75);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProcessEntry, Integer> id = new TableColumn<>("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ProcessEntry, String> startTime = new TableColumn<>("StartTime");
        startTime.setPrefWidth(75);
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<ProcessEntry, String> base = new TableColumn<>("Base");
        base.setPrefWidth(75);
        base.setCellValueFactory(new PropertyValueFactory<>("base"));

        TableColumn<ProcessEntry, String> size = new TableColumn<>("Size");
        size.setPrefWidth(75);
        size.setCellValueFactory(new PropertyValueFactory<>("size"));

        processTable.getColumns().addAll(name, id, startTime, base, size);
    }

    public void update(MemoryEvent event) {

        ObservableList<ProcessEntry> processEntries =
                FXCollections.observableArrayList(event.getProcesses().stream().map(proc -> {
                    return new ProcessEntry(proc, "megabyte");
                }).collect(Collectors.toList()));

        this.processTable.setItems(processEntries);
    }

    public void linkChunkToRow(Rectangle chunk, Process p, Consumer<Rectangle> activeFunc) {
        Optional<ProcessEntry> matchedEntry = processTable
                .getItems()
                .stream()
                .filter(pEntry -> pEntry.getId() == p.getProcId())
                .findFirst();

        if (matchedEntry.isPresent()) {

            linkChunkToRow(chunk, matchedEntry.get(), activeFunc);
        } else {
            System.err.print("No tableview entry matches our process, "
                    + "something is wrong");
        }
    }

    public void linkChunkToRow(Rectangle chunk, ProcessEntry entry, Consumer<Rectangle> activeFunc) {

        chunk.setOnMouseClicked(event -> {
            processTable
                    .getSelectionModel()
                    .select(entry);

            activeFunc.accept(chunk);
        });
    }


    public TableView<ProcessEntry> getTable() {
        return this.processTable;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
