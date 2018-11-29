package model;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem.ProcessSort;
import oshi.software.os.OperatingSystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LocalSource implements ProcessSource {

    private static final OperatingSystem os = new SystemInfo()
            .getOperatingSystem();

    private static final Random rand = new Random();


    public LocalSource() {

    }

    public void kill(int pid) throws IOException {
        Runtime runTime = Runtime.getRuntime();
        if (os.getFamily().toLowerCase().equals("windows")) {
            runTime.exec("taskkill /PID " + 5512);
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
                .collect(Collectors.toList());
    }

    @Override
    public Process generateProcess() {
        final OSProcess[] procs = getOSProcs();
        final OSProcess proc = procs[rand.nextInt(procs.length)];
        return adaptOshi(proc);
    }

}