package view.component;

import controller.Controller;
import driver.ComponentFactory.CTRL;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.MemoryManager.MemoryEvent;
import model.process.Process;

import javax.management.InstanceNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class RectVisualization implements Initializable {

    // from FXML
    @FXML
    private AnchorPane memoryViewPane;
    @FXML
    private Rectangle memoryRect;

    private Rectangle activeChunk;

    // From init
    @CTRL
    private Controller ctrl;
    private ProcessTable processTableController;

    // init
    public void init(ProcessTable processTableController) {
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

            memoryViewPane.getChildren().add(chunk);

            if (p.equals(memEvent.getLastChanged())) {
                setActiveChunk(chunk);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), chunk);
                scaleTransition.setFromX(1.4);
                scaleTransition.setFromY(1.4);
                scaleTransition.setToX(1);
                scaleTransition.setToY(1);
                scaleTransition.setInterpolator(Interpolator.EASE_IN);
                scaleTransition.play();
            }

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
