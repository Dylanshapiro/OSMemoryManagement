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
                .filter(p -> p.getSize() > 0)
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

    private boolean containsProc(List<Process> procList, Process proc) {
        return procList.stream()
                .anyMatch(p2 -> proc.getProcId() == p2.getProcId());
    }

    @Override
    public void sim() {
        List<Process> updatedList = this.getAll();

        // if updatedList does not contain a process that the old list had,
        // notify controller that the process was deleted
        this.localProcs.stream()
                .filter(p -> !(containsProc(updatedList, p)))
                .forEach(p -> notifyKillProcess(p));

        // if updatedList contains a process that the old list did not,
        // notify controller that a process was added
        updatedList.stream()
                .filter(p -> !(containsProc(this.localProcs, p)))
                .forEach(p -> notifyNewProcess(p));

        this.localProcs = updatedList;
    }

    @Override
    public String toString() {
        return "Local";
    }

    @Override
    void onObserved() {
        localProcs = getAll();

        this.localProcs.forEach(p -> notifyNewProcess(p));
    }
}