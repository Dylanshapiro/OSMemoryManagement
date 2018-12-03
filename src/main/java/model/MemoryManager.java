package model;

import model.Algos.Algo;
import model.Algos.FirstFitAlgo;
import model.process.Process;

import java.util.*;


public class MemoryManager extends MemoryObservable {
    private static Algo memoryAlgo;
    private static HashMap<Integer, Process> processes;
    //size in bytes
    public static int memSize;
    private static MemoryManager memoryManager;
    private HashMap <Long, Long> freeMem;

    private MemoryManager(int memSize, Algo algo){
        super();
        this.memSize = memSize;
        this.memoryAlgo = algo;
        processes = new HashMap<>();
        freeMem = new HashMap<>();
        freeMem.put((long) 0,(long) memSize);
    }
    public static MemoryManager getInstance(){
        if(memoryManager==null){
            int memSize = defaultMemSize();
            Algo algo = new FirstFitAlgo();
            memoryManager=new MemoryManager(memSize,algo);
        }
        return memoryManager;

    }

    public boolean allocate(Process p){
        if(p.getSize()>memSize){
            notifyObserversError("Process exceeds total memory "+p.getName()+", now is the time to panic... ");
        }

        p.setBaseAddress(memoryAlgo.allocateP(p.getSize(),freeMem));
        if(!processes.containsKey(p.getProcId()) && p.getBaseAddress() != null){

            //take end address
            Long endAddress = freeMem.get(p.getBaseAddress());
            //remove partition and split
            freeMem.remove(p.getBaseAddress());
            freeMem.put(p.getBaseAddress() + p.getSize() + 1, endAddress);
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
            //free up memory chunk
            freeMem.put(p.getBaseAddress(),p.getBaseAddress() + p.getSize());
            mergePartitions();
            notifyObservers();
            return true;
        }
        return false;
    }

    public void mergePartitions(){
        for (Long startAddress : freeMem.keySet()) {
            //searches to see if key is the next available address
            //if so, merge both partitions
            Long nextAddress = freeMem.get(startAddress) + 1;
            if (freeMem.keySet().contains(nextAddress)) {

                freeMem.replace(startAddress, freeMem.get(nextAddress));
                freeMem.remove(nextAddress);
                break;
            }

        }
    }
    private static Algo defaultAlgo(){
        return new FirstFitAlgo();
    }

    private static int defaultMemSize() {
        return 8000;
    }

    public void setAlgo(Algo a) {
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
