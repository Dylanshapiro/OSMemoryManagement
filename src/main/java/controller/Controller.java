package controller;

import config.Config;
import javafx.application.Platform;
import model.Algos.*;
import model.*;
import model.MemoryManager.MemoryEvent;
import model.Process;
import view.Display;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Controller implements MemoryObserver {

    List<ProcessSource> sourceList;

    private ScheduledExecutorService execService;

    private final Config config;
    private final ProcessSource source;
    private final MemoryManager manager;
    private final Display view;

    public Controller(MemoryManager manager, Display view,
                      ProcessSource source, Config config) {

        this.config = config;

        execService = Executors.newScheduledThreadPool(1);
        this.sourceList = SourceFactory.initAll(this.getRemoteNodes());

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
        try {
            this.source.kill(p.getProcId());
            this.manager.deallocate(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAlgo(Algo a) {
        this.manager.clearProc();
        this.manager.setAlgo(a);
    }

    public void addProc() {
        Process p = this.source.generateProcess();
        this.manager.allocate(p);
    }

    public void startSim() {
        this.execService = Executors.newScheduledThreadPool(1);

        int delayMs = getDelay();
        int delaySpread = getDelaySpread();

        AtomicInteger thisDelay = new AtomicInteger(delayMs);

        this.execService.scheduleWithFixedDelay(() -> {

            this.manager.allocate(this.source.generateProcess());

        }, 0, thisDelay.get(), TimeUnit.MILLISECONDS);
    }

    public void stopSim() {
        this.execService.shutdown();
    }

    public List<ProcessSource>  getSourceList(){
        return this.sourceList;
    }

    // Config stuff //
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
                    return this.validateIpV4(ipString);
                });

        if (allValid) {
            this.config.trySetSetting("nodes", nodes);
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateIpV4(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    public List<String> getRemoteNodes() {
        return this.config.tryGetSetting("nodes").get();
    }


    static class SourceFactory {

        static List<ProcessSource> sourceList;

        public static List<ProcessSource> initAll(List<String> ips) {

            sourceList = new ArrayList<>(16);

            sourceList.add(new SimSource(1));
            sourceList.add(new LocalSource());

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
                return new RemoteSource(ip);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}