package Model;

import Model.Algos.Algo;
import Model.Algos.*;

import java.util.ArrayList;
import java.util.List;


public class MemoryManager extends MemoryObservable {
    private static Algo memoryAlgo;
    private static List<Process> processes;
    //size in bytes
    public static int memSize;
    private static MemoryManager memoryManager;


    private MemoryManager(int memSize, Algo algo){
        super();
        this.memSize=memSize;
        this.memoryAlgo=algo;
        processes=new ArrayList<>();
    }
    public static MemoryManager getInstance(){
        if(memoryManager==null){
            int memSize = defaultMemSize();
            Algo algo = new FirstFitAlgo(memSize);
            memoryManager=new MemoryManager(memSize,algo);
        }
        return memoryManager;

    }

    public List<Process> getAllProc(){
        return this.processes;
    }


    public boolean allocate(Process p){
        //TODO change based on implemenatation
        if(!processes.contains(p)&& memoryAlgo.allocPs(p)!=null){
            processes.add(p);
            notifyObservers();
            return true;
        }
        return false;
    }
    public boolean deallocate(Process p){
        if(processes.contains(p)) {
            boolean result= processes.remove(p);
            notifyObservers();
            return result;
        }
        return false;
    }
    private static Algo defaultAlgo(){
        return new FirstFitAlgo(memSize);
    }

    private static int defaultMemSize() {
        return 655360;
    }

    public void setAlgo(Algo memoryAlgo) {
        this.memoryAlgo = memoryAlgo;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        MemoryManager.memSize = memSize;
    }

    private void notifyObservers(){
        this.notifyObservers(new MemoryEvent(processes,memSize));
    }

    public class MemoryEvent{
        private List<Process> processes;
        public int memSize;

        public MemoryEvent(List<Process> processes, int memSize) {
            this.processes = processes;
            this.memSize = memSize;
        }

        public List<Process> getProcesses() {
            return processes;
        }

        public int getMemSize() {
            return memSize;
        }
    }
}
