package Controller;

import Controller.*;
import Model.*;
import View.*;

public class Driver {

    public static void main(String[] args){
        init();
    }

    public static void init(){
        MemoryManager memMan = MemoryManager.getInstance();
        memMan.setAlgo(new FirstFitAlgo(memMan.getMemSize()));

        Controller controller = new Controller(
                    new SimSource(20),
                    new Display(),
                    memMan
            );

        controller.run();

    }

}