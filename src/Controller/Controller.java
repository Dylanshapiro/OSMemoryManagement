package Controller;

import Model.*;
import View.*;

import java.util.Observer;

public class Controller extends Observer {

    private ProcessSource source;
    private Display view;
    private MemoryManager manager;

    public Controller(ProcessSource source, Display view,
                      MemoryManager manager){

        this.source = source;
        this.view = view;
        this.manager = manager;

        this.manager.addObserver(this);
    }

    // TODO make Config class to set to display
    public void setDisplay(String config){
        this.view.updateDisplay(config);
    }

    public void update(Observer, Object MemEvent){

    }

    void killProc(Process pid){
        manager.deallocate(pid);
    }

    void run(){

    }
}
