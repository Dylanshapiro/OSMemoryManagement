package controller;

import config.Config;
import javafx.application.Platform;
import model.Algos.*;
import model.*;
import model.MemoryManager.MemoryEvent;
import model.Process;
import sun.net.util.IPAddressUtil;
import view.Display;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller implements MemoryObserver {

    private ScheduledExecutorService execService;

    private final Config config;
    private final ProcessSource source;
    private final MemoryManager manager;
    private final Display view;

    public Controller(MemoryManager manager, Display view,
                      ProcessSource source, Config config) {

        execService = Executors.newScheduledThreadPool(1);
        this.config = config;
        this.source = source;
        this.manager = manager;
        this.view = view;
        this.manager.addObserver(this);
    }

    // receive from Observable
    public void update(MemoryObservable obs, MemoryEvent memEvent) {
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

    // input api
    public void killProc(Process p) {
        this.manager.deallocate(p);
    }

    public void setAlgo(Algo a) {
        this.manager.clearProc();
        this.manager.setAlgo(a);
    }

    public void addProc() {
        this.manager.allocate(this.source.generateProcess());
    }

    public void startSim() {
        this.execService = Executors.newScheduledThreadPool(1);

        int delayMs = getDelay();
        int delaySpread = getDelaySpread();

        AtomicInteger thisDelay = new AtomicInteger(delayMs);

        this.execService.scheduleWithFixedDelay(() -> {

            thisDelay.set(ThreadLocalRandom.current().nextInt(
                    (delayMs - delaySpread / 2),
                    (delayMs + delaySpread / 2)));

            manager.allocate(source.generateProcess());
        }, 0, thisDelay.get(), TimeUnit.MILLISECONDS);

    }

    public void stopSim() {
        this.execService.shutdown();
    }

    // Config stuff
    // set the spawn rate for sim source
    public void setDelay(int delay) {
        this.config.trySetSetting("delay",
                Integer.toString(delay));
    }

    public int getDelay() {
        return Integer.parseInt(this.config.tryGetSetting("delay")
                .get().get(0));
    }

    // set the spawn rate for sim source
    public void setDelaySpread(int delayS) {
        this.config.trySetSetting("delaySpread",
                Integer.toString(delayS));
    }

    public int getDelaySpread() {
        return Integer.parseInt(this.config.tryGetSetting("delaySpread")
                .get().get(0));
    }

    public void setSizeSpread(int sizeSpread) {
        this.config.trySetSetting("sizeSpread",
                Integer.toString(sizeSpread));
    }

    public int getSizeSpread() {
        return Integer.parseInt(this.config.tryGetSetting("sizeSpread")
                .get().get(0));
    }

    public void setVariance(int variance) {
        this.config.trySetSetting("variance",
                Integer.toString(variance));
    }

    public int getVariance() {
        return Integer.parseInt(this.config.tryGetSetting("variance")
                .get().get(0));
    }

    /**
     * returns false if not valid ip's
     *
     * @return boolean
     */
    public boolean setNodes(List<String> nodes) {

        boolean allValid = nodes.stream()
                .allMatch(ipString -> {
                    return IPAddressUtil.isIPv4LiteralAddress(ipString) ||
                            IPAddressUtil.isIPv6LiteralAddress(ipString);
                });

        if (allValid) {
            this.config.trySetSetting("nodes", nodes);
            return true;
        } else {
            return false;
        }
    }

    public List<InetAddress> getRemoteNodes() {
        return this.config.tryGetSetting("nodes").get()
                .stream()
                .map(ipString -> tryMapIp(ipString))
                .collect(Collectors.toList());
    }

    private InetAddress tryMapIp(String ipString) {
        try {
            return InetAddress.getByName(ipString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null; // should never actually be null
        // setter setNodes() will not set a
        // value that isnt valid
    }
}