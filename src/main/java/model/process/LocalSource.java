package model.process;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessSort;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LocalSource extends ProcessSourceObservable implements ProcessSource {

    private int id;

    private static final OperatingSystem os = new SystemInfo()
            .getOperatingSystem();

    private static final Random rand = new Random();

    List<Process> localProcs;

    public LocalSource(int id) {
        this.id = id;
        this.localProcs = getAll();
    }

    public void kill(int pid) throws IOException {
        Runtime runTime = Runtime.getRuntime();
        if (os.getFamily().toLowerCase().equals("windows")) {
            runTime.exec("taskkill /PID " + pid);
        } else {
            runTime.exec("kill " + pid);
        }
    }

    private OSProcess[] getOSProcs() {
        return os.getProcesses(os.getProcessCount(), ProcessSort.PID);
    }

    private Process adaptOshi(OSProcess p) {
        return new Process(p.getName(),
                p.getProcessID(),
                p.getStartTime(),
                p.getResidentSetSize());
    }

    @Override
    public List<Process> getAll() {
        return Arrays.stream(getOSProcs())
                .map(this::adaptOshi)
                .filter(p -> !p.getName().toLowerCase().equals("ps"))
                .collect(Collectors.toList());
    }

    @Override
    public Process generateProcess() {
        final OSProcess[] procs = getOSProcs();
        final OSProcess proc = procs[rand.nextInt(procs.length)];
        return adaptOshi(proc);
    }

    @Override
    public int getId() {
        return this.id;
    }

    private boolean oldProc(Process p1) {
        return this.localProcs.stream()
                .anyMatch(p2 -> p2.getProcId() == p1.getProcId());
    }


    private void update() {
        List<Process> updatedList = this.getAll();

        List<Process> deleted = this.localProcs
                .stream()
                .filter(p -> {
                    return updatedList.
                            stream()
                            .noneMatch(p2 -> p.getProcId() == p2.getProcId());

                })
                .collect(Collectors.toList());


        List<Process> added = updatedList
                .stream()
                .filter(p -> {
                    return this.localProcs
                            .stream()
                            .noneMatch(p2 -> p.getProcId() == p2.getProcId());
                }).collect(Collectors.toList());

        this.localProcs = updatedList;


        for (Process p : deleted){
            notifyKillProcess(p);
        }

        for(Process p : added){
            notifyNewProcess(p);
        }

    }

    public void sim() {
        this.update();
    }

    @Override
    public String toString() {
        return "Local";
    }

    @Override
    void onObserved() {
        localProcs = getAll();

        for (Process p: this.localProcs){
            notifyNewProcess(p);
        }
    }
}