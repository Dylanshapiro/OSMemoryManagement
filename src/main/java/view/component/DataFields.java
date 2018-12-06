package view.component;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.MemoryManager.MemoryEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DataFields implements Initializable {


    // from FXML
    @FXML
    private Label curSourceText;

    @FXML
    private Label curAlgoText;

    @FXML
    private Label curProcNumText;

    @FXML
    private Label curMemUsedText;

    @FXML
    private Label totalMemoryText;

    // From init
    private Controller ctrl;


    // init
    public void init(Controller ctrl) {
        this.ctrl = ctrl;

        this.setProcNumText(0);
        this.setUsedMemoryText("0");
        this.setTotalMemoryText(String.valueOf(this.ctrl.getMemSize()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void update(MemoryEvent event){
        this.setProcNumText(event.getProcesses().size());
        this.setUsedMemoryText("" + calcUsedMem(event));
    }

    public void setProcNumText(int num){
        this.curProcNumText.setText(toString().valueOf(num));
    }

    public void setSourceText(String source) {
        this.curSourceText.setText(source);
    }

    public void setAlgoText(String algo) {
        this.curAlgoText.setText(algo);
    }

    public void setUsedMemoryText(String used) {
        this.curMemUsedText.setText(used);
    }

    public void setTotalMemoryText(String total) {
        this.totalMemoryText.setText(total);
    }

    private Long calcUsedMem(MemoryEvent event) {

        return event.getProcesses().stream()
                .mapToLong(proc -> proc.getSize())
                .reduce((acc, cur) -> {
                    return acc + acc;
                }).orElseGet( () -> new Long(0));
    }

}
