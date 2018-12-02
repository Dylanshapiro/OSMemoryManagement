package model;

import model.Algos.Algo;
import model.Algos.FirstFitAlgo;
import model.process.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MemoryManager extends MemoryObservable {
    private static Algo memoryAlgo;
    private static HashMap<Integer, Process> processes;
    //size in bytes
    public static int memSize;
    private static MemoryManager memoryManager;

    private MemoryManager(int memSize, Algo algo){
        super();
        this.memSize = memSize;
        this.memoryAlgo = algo;
        processes = new HashMap<>();
    }
    public static MemoryManager getInstance(){
        if(memoryManager==null){
            int memSize = defaultMemSize();
            Algo algo = new FirstFitAlgo(memSize);
            memoryManager=new MemoryManager(memSize,algo);
        }
        return memoryManager;

    }

    public boolean allocate(Process p){
        if(p.getSize()>memSize){
            notifyObserversError("Process exceeds total memory "+p.getName()+", now is the time to panic... ");
        }

        boolean[] mem;

        if(!processes.containsKey(p.getProcId()) && (memoryAlgo.allocate(p)) != false ){
            processes.put(p.getProcId(),p);
            notifyObservers();
            return true;
        }
        else{
            notifyObserversError("Not enough continuous memory to Allocate process: "+p.getName());
        }
        return false;
    }

    public Process getProcess(int procID) {

        return processes.get(procID);
    }

    public boolean deallocate(int procID){
        Process p;
        if((p = getProcess(procID)) != null) {
            processes.remove(procID);
            memoryAlgo.deallocate(p);
            notifyObservers();
            return true;
        }
        return false;
    }
    private static Algo defaultAlgo(){
        return new FirstFitAlgo(memSize);
    }

    private static int defaultMemSize() {
        return 65536011;
    }

    public void setAlgo(Algo a) {
        a.setMemoryState(this.memoryAlgo.getMemoryState());
        this.memoryAlgo = a;
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

    public void clearProc() {
        this.processes.clear();
        notifyObservers();
    }

    public class MemoryEvent{
        private HashMap<Integer, Process> processes;
        public int memSize;

        public MemoryEvent(HashMap<Integer, Process> processes, int memSize) {
            this.processes = processes;
            this.memSize = memSize;
        }

        public List<Process> getProcesses() {

            return new ArrayList<Process>(processes.values());
        }

        public int getMemSize() {
            return memSize;
        }
    }
}
