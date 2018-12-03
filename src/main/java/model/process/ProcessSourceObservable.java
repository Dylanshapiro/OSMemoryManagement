package model.process;

import model.MemoryManager;
import model.MemoryObserver;

import java.util.LinkedList;
import java.util.List;

import static javafx.scene.input.KeyCode.O;

public class ProcessSourceObservable {
    private List<ProcessSourceObserver> observers;
    public ProcessSourceObservable(){
        observers=new LinkedList<>();
    }
    public void addObserver(ProcessSourceObserver obs){
        observers.add(obs);
    }
    public void removeObserver(ProcessSourceObserver obs){
        observers.remove(obs);
    }
    protected void notifyNewProcess(Process p){
        for(ProcessSourceObserver o:observers){
            o.newProcess(p);
        }
    }
    protected void notifyKillProcess(Process p){
        for(ProcessSourceObserver o:observers){
            o.killProcess(p);
        }
    }
}
