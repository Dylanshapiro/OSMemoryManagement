package Model;

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
           procNames = Files.readAllLines(Paths.get("Resources\\names.txt"), StandardCharsets.UTF_8);

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
    public void simProcess()
    {
        Random rand = new Random();
        int memSize = 0;

        for(int i = 1; i <= numOfProcess; i++) {
            memSize = getRandomIntBetweenRange(1, 5000);
            processList.add(new Process(procNames.get(rand.nextInt(procNames.size())),i, i, memSize));
        }
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
