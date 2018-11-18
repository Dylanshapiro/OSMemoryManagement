package Controller;

import Model.*;
import View.*;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {

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
        this.view.updateDisplay();
    }

    public void update(Observable obs, Object MemEvent){

    }

    void killProc(Model.Process pid){
        manager.deallocate(pid);
    }

    void run(){
          this.source.simProcess();
          for (Model.Process p : this.source.getAll()){
              this.manager.allocate(p);
          }

          this.manager.getAllProc()
                  .forEach(proc -> System.out.println("Process: " +
                          proc.getProcId() + ", "+ proc.getSize()));
    }
}
