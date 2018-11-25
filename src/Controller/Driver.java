package Controller;

import Model.Algos.FirstFitAlgo;
import Model.MemoryManager;
import Model.ProcessSource;
import Model.SimSource;
import View.Display;
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

        // Init MemoryManager
        MemoryManager menMan = MemoryManager.getInstance();
        menMan.setAlgo(new FirstFitAlgo(menMan.getMemSize()));

        // Init a default process source
        ProcessSource source = new SimSource(1);

        Display view = new Display();                           // init view
        Controller ctrl = new Controller(menMan, view, source); // compose Controller
        view.setCtrl(ctrl);                                     // give view the ref it needs

        // Load jfx view. Set controller
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/Controller/view.fxml"));
        fxmlLoader.setController(view);
        Parent root = fxmlLoader.load();

        // set scene and show
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}