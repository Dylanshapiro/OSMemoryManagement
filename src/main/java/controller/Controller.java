package controller;

import javafx.application.Platform;
import model.Algos.*;
import model.MemoryManager;
import model.MemoryManager.MemoryEvent;
import model.MemoryObservable;
import model.MemoryObserver;
import model.process.Process;
import model.process.ProcessSource;
import model.process.ProcessSourceObserver;
import view.component.Root;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Controller implements MemoryObserver, ProcessSourceObserver {


    private Optional<ScheduledFuture> handle;
    private ScheduledExecutorService execService;

    private List<ProcessSource> sourceList;
    private ProcessSource source;

    private final MemoryManager manager;
    private final Root view;

    public Controller(MemoryManager manager, Root view, List<ProcessSource> pList) {

        this.handle = Optional.empty();
        this.execService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });

        this.manager = manager;
        this.view = view;
        this.sourceList = pList;

        this.source = sourceList.get(0);
    }

    // receive from Observable
    public void update(MemoryObservable obs, MemoryEvent memEvent) {
        Platform.runLater(() -> {
            this.view.updateDisplay(memEvent);// send update to view
        });
    }

    public List<ProcessSource> getSourceList() {
        return this.sourceList;
    }

    @Override
    public void error(String err) {
        System.out.println(err);
    }

    // utils
    public List<Algo> getAlgoList() {
        final long memSize = this.manager.getMemSize();

        return Arrays.asList(
                new FirstFitAlgo(memSize),
                new BestFitAlgo(memSize),
                new WorstFitAlgo(memSize),
                new NextFitAlgo(memSize),
                new BuddyAlgo(memSize)
        );
    }

    public long getMemSize() {
        return this.manager.getMemSize();
    }

    // input api

    public void setSource(String id) throws InstanceNotFoundException {
        setSource(Integer.parseInt(id));
    }

    public void setSource(int id) throws InstanceNotFoundException {

        ProcessSource newSource = this.sourceList.stream()
                .filter(proc -> proc.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    return new InstanceNotFoundException("Process Source was not found");
                });

        this.manager.clearProc();
        this.source = newSource;
    }

    public void killProc(int procID) {
        try {
            this.source.kill(procID);
            this.manager.deallocate(procID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAlgo(Algo a) {
        this.manager.setAlgo(a);
    }

    public void addProc() {
        this.execService.execute(() -> {
            Process p = this.source.generateProcess();
            this.manager.allocate(p);
        });
    }

    public void startSim() {

        ScheduledFuture<?> handle = this.execService.scheduleWithFixedDelay(() -> {

            this.source.sim();

        }, 0, 600, TimeUnit.MILLISECONDS);

        this.handle = Optional.of(handle);

    }

    public void stopSim() {
        if (this.handle.isPresent()) {
            this.handle.get().cancel(false);
            this.handle = Optional.empty();
        }
    }

    @Override
    public void newProcess(Process p) {
        execService.execute(() -> {
            manager.allocate(p);
        });
    }

    @Override
    public void killProcess(Process p) {
        execService.execute(() -> {
            manager.deallocate(p);
        });
    }

}