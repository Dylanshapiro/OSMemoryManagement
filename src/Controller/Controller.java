package Controller;

import Model.*;
import Model.Algos.FirstFitAlgo;
import Model.Process;
import View.*;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller implements MemoryObserver {

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

    public void update(MemoryObservable obs, MemoryManager.MemoryEvent MemEvent){
        List<Process> procs = ((MemoryManager) obs).getAllProc();

        procs.stream()
                .sorted(Comparator.comparingInt(p -> p.getBaseAddress().get()))
                .skip(procs.size() - 1)
                .forEach(proc ->
                        System.out.printf("#%4d, %s%-2s [ %-4d %-4d ] \n",
                                procs.size(),
                                "Id: ",
                                proc.getProcId(),
                                proc.getBaseAddress().get().intValue(),
                                proc.getBaseAddress().get().intValue() +
                                        ((proc.getSize() > FirstFitAlgo.KILOBYTE) ? proc.getSize() /
                                                           FirstFitAlgo.KILOBYTE : 1)));
    }

    void killProc(Model.Process pid){
        manager.deallocate(pid);
    }

    public static final int GEN_DELAY = 500;

    public void run() {
        ScheduledExecutorService schedExec =
                Executors.newScheduledThreadPool(1);

        schedExec.scheduleWithFixedDelay(() -> {
            source.simProcess();
            manager.allocate(source.getAll()
                    .stream()
                    .skip(source.getAll()
                            .size() -1)
                    .findFirst()
                    .get());  // Need Easier way to gen one unique process
                              // on the spot
        }, 0, GEN_DELAY, TimeUnit.MILLISECONDS);

    }


}
