package controller;

import javafx.application.Platform;
import model.Algos.*;
import model.MemoryManager;
import model.MemoryManager.MemoryEvent;
import model.MemoryObservable;
import model.MemoryObserver;
import model.process.Process;
import model.process.ProcessSource;
import model.process.ProcessSourceObservable;
import model.process.ProcessSourceObserver;
import view.component.Root;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

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

        ((ProcessSourceObservable) this.source).removeObserver(this);

        this.manager.clearProc();

       this.source = this.sourceList.stream()
                .filter(proc -> proc.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    return new InstanceNotFoundException("Process Source was not found");
                });

       ((ProcessSourceObservable) this.source).addObserver(this);
    }

    public void setAlgo(Algo a) {
        this.manager.setAlgo(a);
    }

    public void startSim() {

        ScheduledFuture<?> handle = this.execService.scheduleWithFixedDelay(() -> {

            this.source.sim();

        }, 0, 300, TimeUnit.MILLISECONDS);

        this.handle = Optional.of(handle);

    }

    public void stopSim() {
        this.handle.ifPresent(handle -> {
            handle.cancel(false);
            this.handle = Optional.empty();
        });
    }

    public void addProc() {
        CompletableFuture.supplyAsync(() -> this.source.generateProcess(), execService)
                .thenAccept(p -> this.manager.allocate(p));
    }

    @Override
    public void newProcess(Process p) {
        CompletableFuture.runAsync(() -> this.manager.allocate(p), execService);
    }

    public void killProc(int procID) {
        CompletableFuture.runAsync(() ->{
            try {
                this.source.kill(procID);
                this.manager.deallocate(procID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, execService);
    }

    @Override
    public void killProcess(Process p) {
        CompletableFuture.runAsync(() ->{
            manager.deallocate(p.getProcId());
        }, execService);
    }

}