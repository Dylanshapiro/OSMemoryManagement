package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SimSource implements ProcessSource{

    //ArrayList of processes
    private ArrayList<Process> processList = new ArrayList<Process>();

    //Number of process to sim
    private int numOfProcess;

    //simulated bank of names
    private List<String> procNames;

    public SimSource(int num)
    {
        numOfProcess = num;
        //Prepare process name bank
        try {
           procNames = Files.readAllLines(Paths.get("./src/main/resources/names.txt"), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            System.out.println("Process \"names.txt\" not found!");
        }
    }

    @Override
    public void  kill(int pid){
        processList.removeIf(proc -> proc.getProcId() == pid );
    }

    @Override
    public List<Process> getAll()
    {
        return processList;
    }

    //Random number between two values
    public int getRandomIntBetweenRange(int min, int max)
    {
        Random rand = new Random();

        int n = rand.nextInt(max) + min;
        return n;
    }

    //Create the processes of random size and name
    @Override
    public Process generateProcess()
    {
        Random rand = new Random();
        long memSize = 0;

        memSize = getRandomIntBetweenRange(10, 30) * 1024;
        Process proc = new Process(procNames.get(rand.nextInt(procNames.size())), ++numOfProcess,
                                   System.currentTimeMillis(), memSize);
        processList.add(proc);
        return proc;

    }

    @Override
    public String toString(){
        return "Simulated";
    }

}
