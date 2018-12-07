package driver;

import controller.Controller;
import model.Algos.FirstFitAlgo;
import model.process.LocalSource;
import model.MemoryManager;
import model.process.ProcessSource;
import model.process.ProcessSourceObservable;
import model.process.SimSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Driver extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("../xml/Root.fxml"));


        Controller ctrl = initController();

        loader.setControllerFactory(new ComponentFactory(ctrl));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        scene.getStylesheets().add("css/root.css"); // load css
        scene.getStylesheets().add("css/split-pane.css"); // load css
        scene.getStylesheets().add("css/alloc-bar.css"); // load css
        scene.getStylesheets().add("css/table-view.css"); // load css

        // set scene and show
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSMM");

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(640);
        primaryStage.show();
    }

    private Controller initController() {
        MemoryManager manager = MemoryManager.getInstance();
        manager.setAlgo(new FirstFitAlgo(600000));

        List<ProcessSource> pList = initSources();

        Controller ctrl = new Controller(manager, pList);

        manager.addObserver(ctrl);
        ((ProcessSourceObservable) pList.get(0)).addObserver(ctrl);
        return ctrl;
    }

    public List<ProcessSource> initSources() {
        List<ProcessSource> procs = new ArrayList<>(4);
        procs.add(new SimSource(100, 1));
        procs.add(new LocalSource(2));
        return procs;
    }

}