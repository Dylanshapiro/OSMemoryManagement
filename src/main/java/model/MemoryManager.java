package model;

import model.Algos.Algo;
import model.Algos.*;
import model.process.Process;

import java.util.*;


public class MemoryManager extends MemoryObservable {
    private static Algo memoryAlgo;
    private static List<Process> processes;
    //size in bytes
    public static long memSize;
    private static MemoryManager memoryManager;


    private MemoryManager(long memSize, Algo algo){
        super();
        this.memSize=memSize;
        this.memoryAlgo=algo;
        processes=new ArrayList<>();
    }
    public static MemoryManager getInstance(){
        if(memoryManager==null){
            long memSize = defaultMemSize();
            Algo algo = new FirstFitAlgo(memSize);
            memoryManager=new MemoryManager(memSize,algo);
        }
        return memoryManager;

    }

    public List<Process> getAllProc(){
        return this.processes;
    }

    public boolean allocate(Process p){
        if(p.getSize()>memSize){
            this.notifyObservers(getErrorEvent(p,
                    "Process exceeds total memory "+p.getName()+", now is the time to panic... "));
        }
        Long result=memoryAlgo.allocPs(p);
        if(!processes.contains(p)&& result!=null){
            processes.add(p);
            Collections.sort(processes, new Comparator<Process>() {
                @Override
                public int compare(Process process, Process t1) {
                    if(process.getBaseAddress().get()<t1.getBaseAddress().get()){
                        return -1;
                    }
                    else{
                        return 1;
                    }
                }
            });
            notifyObservers(p,Op.ADD);
            return true;
        }
        else{
            this.notifyObservers(getErrorEvent(p,
                    "Not enough continuous memory to Allocate process: "+p.getName()));
        }
        return false;
    }


    public boolean deallocate(Process p){
        if(processes.contains(p)) {
            boolean result= processes.remove(p);
            memoryAlgo.deallocate(p);
            notifyObservers(p, Op.KILL);
            return result;
        }
        return false;
    }
    public Process getProcess(int procID) {

        for(Process p:processes){
            if(p.getProcId()==procID){
                return p;
            }
        }
        return null;
    }

    public boolean deallocate(int procID){
        Process p;
        if((p = getProcess(procID)) != null) {
            processes.remove(p);
            memoryAlgo.deallocate(p);
            notifyObservers(p, Op.KILL);
            return true;
        }
        return false;
    }

    private static Algo defaultAlgo(){
        return new FirstFitAlgo(memSize);
    }

    private static long defaultMemSize() {
        return 17179869184L;
    }

    public void setAlgo(Algo memoryAlgo) {
        this.memoryAlgo = memoryAlgo;
        memoryAlgo.setMemSize(memSize);
        this.memoryAlgo.setRepresentation(processes);
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        MemoryManager.memSize = memSize;
    }

    private void notifyObservers(Process p,Op type){
        this.notifyObservers(new MemoryEvent(processes,p,memSize, type));
    }

    private void notifyObservers(Process p, Op type, String message){
        final MemoryEvent event = new MemoryEvent(processes, p, memSize, type);
        event.setStatusMessage(message);
        this.notifyObservers(event);
    }

    public void reset() {
        this.processes.clear();
        Process temp=null;
        memoryAlgo.setRepresentation(new ArrayList<>());
        notifyObservers(temp, Op.RESET, "sim reset");
    }

    public void clearProc() {
        this.processes.clear();
        Process temp=null;
        notifyObservers(temp,Op.RESET);
    }

   public enum Op{
        ADD,
        KILL,
        RESET,
        ERROR,
    }

    public class MemoryEvent{

        private List<Process> processes;
        public long memSize;
        private Process lastChanged;
        private Op type;
        private Optional<String> statusMessage;

        public MemoryEvent(List<Process> processes,Process process,
                           long memSize, Op type) {
            this.processes = processes;
            this.memSize = memSize;
            this.lastChanged=process;
            this.type = type;
            this.statusMessage = Optional.empty();
        }



        public void setStatusMessage(String message){
            this.statusMessage = Optional.of(message);
        }

        public List<Process> getProcesses() {
            return processes;
        }

        public Process getLastChanged() {
            return lastChanged;
        }

        public long getMemSize() {
            return memSize;
        }

        public long getUsedMem(){
            return this.processes.stream()
                    .mapToLong(Process::getSize)
                    .sum();
        }

        public long getAvailMem(){
            return this.memSize - this.getUsedMem();
        }

        public Op getOpType(){
            return this.type;
        }

        public Optional<String> getStatusMessage(){
            return this.statusMessage;
        }
    }

    private MemoryEvent getErrorEvent(Process p, String message){
        MemoryEvent event = new MemoryEvent(processes,p,memSize, Op.ERROR);
        event.setStatusMessage( message );
        return event;
    }
}
