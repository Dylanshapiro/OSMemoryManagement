package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MemoryManager {
    private static Algo memoryAlgo;
    private static List<Process> processes;
    //size in bytes
    public static long memSize;
    private static MemoryManager memoryManager;
    private Observable obs;

    private MemoryManager(Algo algo,long memSize){
        this.memoryAlgo=algo;
        this.memSize=memSize;
        obs=new Observable();
        processes=new ArrayList<>();
    }
    public static MemoryManager getInstance(){
        if(memoryManager==null){
            memoryManager=new MemoryManager(defaultAlgo(),defaultMemSize());
        }
        return memoryManager;

    }
    public boolean allocate(Process p){
        //TODO change based on implemenatation
        if(memoryAlgo.allocPs(processes, memSize,p)!=null){
            processes.add(p);
            return true;
        }
        return false;
    }
    public boolean deallocate(Process p){
        return processes.remove(p);
    }
    private static Algo defaultAlgo(){
        return new FirstFitAlgo();
    }
    private static long defaultMemSize() {
        return 1024;
    }

    public void setAlgo(Algo memoryAlgo) {
        this.memoryAlgo = memoryAlgo;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        MemoryManager.memSize = memSize;
    }
    public void addObserver(Observer obs){
        this.obs.addObserver(obs);
    }
    public void removeObserver(Observer obs){
        this.obs.deleteObserver(obs);
    }
    private void notifyObservers(){
        this.obs.notifyObservers(new MemoryEvent(processes,memSize));
    }
    public class MemoryEvent{
        private List<Process> processes;
        public long memSize;

        public MemoryEvent(List<Process> processes, long memSize) {
            this.processes = processes;
            this.memSize = memSize;
        }

        public List<Process> getProcesses() {
            return processes;
        }

        public long getMemSize() {
            return memSize;
        }
    }
}
