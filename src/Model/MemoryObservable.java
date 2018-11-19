package Model;

import java.util.LinkedList;
import java.util.List;

public abstract class MemoryObservable {
    private List<MemoryObserver> observers;
    public MemoryObservable(){
        observers=new LinkedList<>();
    }
    public void addObserver(MemoryObserver obs){
        observers.add(obs);
    }
    public void removeObserver(MemoryObserver obs){
        observers.remove(obs);
    }
    protected void notifyObservers(MemoryManager.MemoryEvent event){
        for(MemoryObserver obs:observers){
            obs.update(this,event);
        }
    }


}
