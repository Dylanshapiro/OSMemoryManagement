package controller;

import config.Config;
import javafx.application.Platform;
import model.Algos.*;
import model.*;
import model.MemoryManager.MemoryEvent;
import model.process.*;
import model.process.Process;
import sun.net.util.IPAddressUtil;
import view.Display;

import javax.management.InstanceNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.ArrayList;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller implements MemoryObserver, ProcessSourceObserver {


    private Optional<ScheduledFuture> handle;
    private ScheduledExecutorService execService;

    private List<ProcessSource> sourceList;
    private ProcessSource source;

    private final MemoryManager manager;
    private final Display view;

    public Controller(MemoryManager manager, Display view, List<ProcessSource> pList) {

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

        }, 0, 300, TimeUnit.MILLISECONDS);

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