package controller;

import model.Algos.FirstFitAlgo;
import model.process.LocalSource;
import model.MemoryManager;
import model.process.ProcessSource;
import model.process.ProcessSourceObservable;
import model.process.SimSource;
import view.component.Root;
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

        // parse out saved preferences from config file

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("../xml/Root.fxml"));

        Parent root = loader.load();

        MemoryManager manager = MemoryManager.getInstance();
        manager.setAlgo(new FirstFitAlgo(600000));
        List<ProcessSource> pList = initSources();

        Root view = loader.getController();

        Controller ctrl = new Controller(manager,view,pList);
        manager.addObserver(ctrl);
        ((ProcessSourceObservable) pList.get(0)).addObserver(ctrl);
        view.setCtrl(ctrl);
        Scene scene = new Scene(root);

        scene.getStylesheets().add("css/root.css"); // load css
        scene.getStylesheets().add("css/split-pane.css"); // load css
        scene.getStylesheets().add("css/alloc-bar.css"); // load css
        scene.getStylesheets().add("css/table-view.css"); // load css



        // set scene and show
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSMM");

        primaryStage.show();
    }


    public List<ProcessSource> initSources(){
        List<ProcessSource> procs = new ArrayList<>(4);
        procs.add(new SimSource(100,1));
        procs.add(new LocalSource(2));
        return procs;
    }
}