package model.process;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimSource extends ProcessSourceObservable implements ProcessSource {

    private int id;
    //ArrayList of processes
    private ArrayList<Process> processList = new ArrayList<Process>();

    private int numOfProcess;

    //simulated bank of names
    private List<String> procNames;

    public SimSource(int num, int id) {
        this.id = id;
        this.numOfProcess = 0;
        //Prepare process name bank
        try {
            procNames = Files.readAllLines(Paths.get("./src/main/resources/names.txt"), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            System.out.println("Process \"names.txt\" not found!");
        }

        this.initProcs(num);
    }

    private void initProcs(int num) {
        for (int i = 0; i < num; i++) {
            this.generateProcess();
        }
    }

    @Override
    public void kill(int pid) {
        processList.removeIf(proc -> proc.getProcId() == pid);
    }

    @Override
    public List<Process> getAll() {
        return processList;
    }

    //Random number between two values
    public int getRandomIntBetweenRange(int min, int max) {
        Random rand = new Random();


        int n = rand.nextInt(max) + min;
        return n;
    }

    //Create the processes of random size and name
    @Override
    public Process generateProcess() {
        Random rand = new Random();
        long memSize = 0;

        memSize = getRandomIntBetweenRange(20, 1000) * 1048576;
        Process proc = new Process(procNames.get(rand.nextInt(procNames.size())), numOfProcess++ ,
                System.currentTimeMillis(), memSize);
        processList.add(proc);
        return proc;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Simulated";
    }

    @Override
    public void sim() {
        int rand = getRandomIntBetweenRange(0, 2);
        if (rand > 0) {
            notifyNewProcess(generateProcess());
        } else {
            rand = getRandomIntBetweenRange(0, processList.size());
            notifyKillProcess(processList.get(rand));
            processList.remove(rand);
        }
    }


    @Override
    void onObserved() {

    }
}
