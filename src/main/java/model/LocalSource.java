package model;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem.ProcessSort;
import oshi.software.os.OperatingSystem;

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