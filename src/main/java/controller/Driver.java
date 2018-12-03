package controller;

import config.Config;
import model.Algos.FirstFitAlgo;
import model.process.LocalSource;
import model.MemoryManager;
import model.process.ProcessSource;
import model.process.SimSource;
import view.Display;
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
        Config.initSettings();

        // Init MemoryManager
        MemoryManager menMan = MemoryManager.getInstance();
        menMan.setMemSize(17179869184L);//4gb
        menMan.setAlgo(new FirstFitAlgo(17179869184L));


        Display view = new Display();
        List<ProcessSource> pList = initSources();

        Controller ctrl = new Controller(menMan, view, pList); // compose Controller



        view.setCtrl(ctrl);

        menMan.addObserver(ctrl);// give view the ref it needs
        ((SimSource) pList.get(0)).addObserver(ctrl);
        // Load jfx view. Set controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("../xml/view.fxml"));
        fxmlLoader.setController(view);
        Parent root = fxmlLoader.load();

        // set scene and show
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/root.css"); // load css
        scene.getStylesheets().add("css/split-pane.css"); // load css
        scene.getStylesheets().add("css/alloc-bar.css"); // load css
        scene.getStylesheets().add("css/table-view.css"); // load css
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSMM");
        primaryStage.show();
    }


    public List<ProcessSource> initSources(){
        List<ProcessSource> procs = new ArrayList<>(4);
        procs.add(new SimSource(1));
        procs.add(new LocalSource(2));
        return procs;
    }
}