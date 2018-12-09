package controller;

import driver.ComponentFactory.VIEW;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Controller implements MemoryObserver, ProcessSourceObserver {


    private Optional<ScheduledFuture> handle;
    private ScheduledExecutorService execService;

    private List<ProcessSourceObservable> sourceList;
    private ProcessSource source;

    private final MemoryManager manager;

    @VIEW
    private Root view;

    public Controller(MemoryManager manager) {

        this.handle = Optional.empty();
        this.execService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });

        this.manager = manager;
        this.sourceList = Arrays.asList(
                ProcessSourceObservable.getSimSource(),
                ProcessSourceObservable.getLocalSource());

        this.source = (ProcessSource) sourceList.get(0);
        ((ProcessSourceObservable) this.source).addObserver(this);
    }


    public List<ProcessSource> getSourceList() {

        return this.sourceList.stream()
                .map(source -> (ProcessSource) source)
                .collect(Collectors.toList());
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

    public void resetSim() {
        CompletableFuture.runAsync(() -> {
            this.manager.reset();
        }, execService);
    }

    public void addProc() {
        CompletableFuture.supplyAsync(() -> this.source.generateProcess(), execService)
                .thenAccept(p -> this.manager.allocate(p));
    }

    public void killProc(int procID) {
        CompletableFuture.runAsync(() -> {
            try {
                this.source.kill(procID);
                this.manager.deallocate(procID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, execService);
    }

    public void setSource(String id) throws InstanceNotFoundException {
        setSource(Integer.parseInt(id));
    }

    public void setSource(int id) throws InstanceNotFoundException {

        ((ProcessSourceObservable) this.source).removeObserver(this);

        this.manager.clearProc();

        this.source = this.sourceList.stream()
                .filter(proc -> ((ProcessSource) proc).getId() == id)
                .findFirst()
                .map(source -> (ProcessSource) source)
                .orElseThrow(() -> {
                    return new InstanceNotFoundException();
                });

        this.resetSim();

        ((ProcessSourceObservable) this.source).addObserver(this);
    }

    public void setAlgo(Algo a) {
        this.manager.setAlgo(a);
    }

    // Callbacks
    @Override
    public synchronized void update(MemoryObservable obs, MemoryEvent memEvent) {
        Platform.runLater(() -> {
            this.view.updateDisplay(memEvent);// send update to view
        });
    }

    @Override
    public void killProcess(Process p) {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> manager.deallocate(p.getProcId()));
        }, execService);
    }

    @Override
    public synchronized void newProcess(Process p) {
        CompletableFuture.runAsync(() ->
                        Platform.runLater(() -> this.manager.allocate(p))
                , execService);
    }
}