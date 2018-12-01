package controller;

import config.Config;
import model.Algos.FirstFitAlgo;
import model.process.LocalSource;
import model.MemoryManager;
import model.process.ProcessSource;
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
        menMan.setAlgo(new FirstFitAlgo(menMan.getMemSize()));

        // Init a default process source
        ProcessSource source = new LocalSource();

        Display view = new Display();                           // init view
        Controller ctrl = new Controller(menMan, view, source); // compose Controller
        view.setCtrl(ctrl);                                     // give view the ref it needs

        // Load jfx view. Set controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("../xml/view.fxml"));
        fxmlLoader.setController(view);
        Parent root = fxmlLoader.load();

        // set scene and show
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/split-pane.css"); // load css
        scene.getStylesheets().add("css/alloc-bar.css"); // load css
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSMM");
        primaryStage.show();
    }
}