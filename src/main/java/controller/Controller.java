package controller;

import config.Config;

import javafx.application.Platform;

import model.Algos.*;
import model.MemoryManager;
import model.MemoryObservable;
import model.MemoryObserver;
import model.process.Process;
import model.process.LocalSource;
import model.process.ProcessSource;
import model.process.RemoteSource;
import model.process.SimSource;

import view.Display;

import javax.management.InstanceNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Controller implements MemoryObserver {

    List<ProcessSource> sourceList;

    private Optional<ScheduledFuture> handle;
    private ScheduledExecutorService execService;


    private ProcessSource source;
    private final MemoryManager manager;
    private final Display view;

    public Controller(MemoryManager manager, Display view) {

        execService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });

        this.sourceList = SourceFactory.initAll(Config.getRemoteNodes());

        this.source = sourceList.get(0);
        this.manager = manager;
        this.view = view;
        this.manager.addObserver(this);
    }

    // receive from Observable
    public void update(MemoryObservable obs, MemoryManager.MemoryEvent memEvent) {
        Platform.runLater(() -> {
            this.view.updateDisplay(memEvent);// send update to view
        });
    }

    @Override
    public void error(String err) {
        System.out.println(err);
    }

    // utils
    public List<Algo> getAlgoList() {
        final int memSize = this.manager.getMemSize();

        return Arrays.asList(
                new FirstFitAlgo(memSize),
                new BestFitAlgo(memSize),
                new WorstFitAlgo(memSize),
                new NextFitAlgo(memSize),
                new BuddyAlgo(memSize)
        );
    }

    public int getMemSize(){
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

            this.manager.allocate(this.source.generateProcess());

        }, 0, 600, TimeUnit.MILLISECONDS);

        this.handle = Optional.of(handle);
    }

    public void stopSim() {
        if (this.handle.isPresent()) {
            this.handle.get().cancel(false);
            this.handle = Optional.empty();
        }
    }

    public List<ProcessSource> getSourceList() {
        return this.sourceList;
    }

    static class SourceFactory {

        static List<ProcessSource> sourceList;
        static int nextId = 0;

        public static List<ProcessSource> initAll(List<String> ips) {

            sourceList = new ArrayList<>(16);

            sourceList.add(new SimSource(nextId));
            nextId++;

            sourceList.add(new LocalSource(nextId));
            nextId++;

            if (!ips.isEmpty()) {
                sourceList.addAll(generateRemotes(ips));
            } else {
                System.err.println("no ip's present");
            }

            return sourceList;
        }

        private static List<ProcessSource> generateRemotes(List<String> ips) {
            return ips.stream()
                    .map(SourceFactory::sourceFromIp)
                    .collect(Collectors.toList());
        }

        // Try to make a source from an ipString, handle errors
        // if impossible
        private static RemoteSource sourceFromIp(String ip) {
            try {
                RemoteSource newRemote = new RemoteSource(ip, nextId);
                nextId++;
                return newRemote;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}