package Controller;

import Model.*;
import Model.Algos.FirstFitAlgo;
import View.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Driver {

    public static void main(String[] args){
        init();
    }

    public static void init(){
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Display view = new Display();

        // Pass a function that instantiates everything to
        // executor to be run within its thread pool
        executor.execute(
                () -> {
                    MemoryManager memMan = MemoryManager.getInstance();
                    memMan.setAlgo(new FirstFitAlgo(memMan.getMemSize()));
                    ProcessSource source = new SimSource(1);

                    Controller controller = new Controller(
                            source,
                            view,
                            memMan);

                    controller.run();
                });
    }

}