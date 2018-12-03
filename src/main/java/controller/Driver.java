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

        // Init a default process source
        ProcessSource source = new SimSource(100);//TODO change back to local

        Display view = new Display();                           // init view
        Controller ctrl = new Controller(menMan, view, source,new Config()); // compose Controller
        view.setCtrl(ctrl);                                     // give view the ref it needs
        ((SimSource) source).addObserver(ctrl);
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
}