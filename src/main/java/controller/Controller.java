package controller;

import model.Algos.*;
import model.*;
import model.MemoryManager.MemoryEvent;
import model.Process;
import view.Display;

import java.util.Arrays;
import java.util.List;

public class Controller implements MemoryObserver {

    private final ProcessSource source;
    private final MemoryManager manager;
    private final Display view;

    public Controller(MemoryManager manager, Display view, ProcessSource source) {
        this.source = source;
        this.manager = manager;
        this.view = view;
        this.manager.addObserver(this);
    }

    // receive from Observable
    public void update(MemoryObservable obs, MemoryEvent memEvent) {

        this.view.updateDisplay(memEvent); // send update to view
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

    // input api
    public void killProc(Process p) {
        this.manager.deallocate(p);
    }

    public void setAlgo(Algo a) {
        this.manager.setAlgo(a);
    }

    public void addProc() {
        this.manager.allocate(source.generateProcess());
    }

}