package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    public Process getProcess(int i)
    {
        return processList.get(i);
    }

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
    public Process generateProcess()
    {
        Random rand = new Random();
        int memSize = 0;

        memSize = getRandomIntBetweenRange(1, 5000);
        Process proc = new Process(procNames.get(rand.nextInt(procNames.size())), ++numOfProcess,
                                   System.currentTimeMillis(), memSize);
        processList.add(proc);
        return proc;

    }

    //Prints out the process list for testing
    public void showList()
    {
        for(int i = 0; i < processList.size(); i++) {
            System.out.println("Name: " + processList.get(i).getName() + "ID: " + processList.get(i).getProcId()
                             + " Size: " + processList.get(i).getSize());
        }
    }

}
