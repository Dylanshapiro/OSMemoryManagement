package driver;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.MemoryManager;
import model.process.ProcessSourceObservable;

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

        scene.getStylesheets().addAll(
                "css/root.css",
                "css/split-pane.css",
                "css/alloc-bar.css",
                "css/table-view.css",
                "css/text-area.css"); // load css

        // set scene and show
        primaryStage.setScene(scene);
        primaryStage.setTitle("OSMM");

        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(640);
        primaryStage.setMaxWidth(640);

        primaryStage.show();
    }

    private Controller initController() {
        MemoryManager manager = MemoryManager.getInstance();

        Controller ctrl = new Controller(manager, ProcessSourceObservable.getSimSource(),
                                                  ProcessSourceObservable.getLocalSource());

        manager.addObserver(ctrl);
        return ctrl;
    }

}