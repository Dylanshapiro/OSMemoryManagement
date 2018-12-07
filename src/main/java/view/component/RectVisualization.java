package view.component;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.MemoryManager.MemoryEvent;
import model.process.Process;
import view.ProcessEntry;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RectVisualization implements Initializable {

    // from FXML
    @FXML
    private AnchorPane memoryViewPane;
    @FXML
    private Rectangle memoryRect;

    private Rectangle activeChunk;

    // From init
    private Controller ctrl;
    private ProcessTable processTableController;

    // init
    public void init(Controller ctrl, ProcessTable processTableController) {
        this.ctrl = ctrl;
        this.processTableController = processTableController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void update(MemoryEvent memEvent) {
        this.deleteChunk();

        for (Process p : memEvent.getProcesses()) {
            double size = (double) p.getSize() / (double) memEvent.getMemSize();
            double baseAddress = (double) p.getBaseAddress().get() / (double) memEvent.getMemSize();

            Rectangle chunk = fillChunk(size, baseAddress);

            try {
                this.processTableController.linkChunkToRow(chunk, p, this::setActiveChunk); // link a chunk to process entry
            } catch (InstanceNotFoundException e) {
                e.printStackTrace();
            }

            if (p.equals(memEvent.getLastChanged())) {
                setActiveChunk(chunk);
            }
            //TODO simulate mouse click to have newly added chunk already selected upon spawning.
            memoryViewPane.getChildren().add(chunk);
        }
    }

    public Rectangle fillChunk(double processSize, double processAddress) {

        Rectangle chunk = new Rectangle();
        chunk.setX(memoryRect.getLayoutX() + (processAddress * memoryRect.getWidth()));
        chunk.setY(memoryRect.getLayoutY());
        chunk.setHeight(memoryRect.getHeight());
        chunk.setWidth(processSize * memoryRect.getWidth());

        return chunk;
    }

    public void setActiveChunk(Rectangle chunk) {
        if (activeChunk != null) {
            activeChunk.setFill(Color.BLACK);
        }

        activeChunk = chunk;
        activeChunk.setFill(Color.BISQUE);
    }

    public void deleteChunk() {
        memoryViewPane.getChildren().remove(2, memoryViewPane.getChildren().size());
    }

}
