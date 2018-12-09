package view.component;

import controller.Controller;
import driver.ComponentFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import model.MemoryManager.MemoryEvent;
import model.process.Process;
import view.ProcessEntry;
import driver.ComponentFactory.CTRL;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class ProcessTable implements Initializable {

    // from fxml
    @FXML
    private TableView<ProcessEntry> processTable;

    // from init
    @CTRL
    private Controller ctrl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    public void  update(MemoryEvent event) {

        ObservableList<ProcessEntry> processEntries =
                FXCollections.observableArrayList(event.getProcesses().stream().map(proc -> {
                    return new ProcessEntry(proc, "megabyte");
                }).collect(Collectors.toList()));

        this.processTable.setItems(processEntries);
    }

    public void linkChunkToRow(Rectangle chunk, Process p, Consumer<Rectangle> activeFunc) throws InstanceNotFoundException {

        ProcessEntry entry = (ProcessEntry) processTable
                .getItems()
                .stream()
                .filter(pEntry -> pEntry.getId() == p.getProcId())
                .findFirst()
                .orElseThrow( () -> new InstanceNotFoundException("the ProcessEntry was not found, unable to link"));

            linkChunkToRow(chunk, entry , activeFunc);
    }

    public void linkChunkToRow(Rectangle chunk, ProcessEntry entry, Consumer<Rectangle> activeFunc) {

        chunk.setOnMouseClicked(event -> {
            processTable
                    .getSelectionModel()
                    .select(entry);

            processTable
                    .scrollTo(entry);

            activeFunc.accept(chunk);
        });
    }


    public TableView<ProcessEntry> getTable() {
        return this.processTable;
    }


}
