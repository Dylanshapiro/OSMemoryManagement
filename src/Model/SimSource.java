package Model;

import java.util.*;

public class SimSource implements ProcessSource{

    //ArrayList of processes
    private ArrayList<Process> processList = new ArrayList<Process>();

    //Number of process to sim
    int numOfProcess;


    public SimSource(int num)
    {
        numOfProcess = num;
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

    //Create the processes of random size
    public void simProcess()
    {
        int memSize = 0;

        for(int i = 1; i <= numOfProcess; i++) {
            memSize = getRandomIntBetweenRange(1, 10000);
            processList.add(new Process(i, i, memSize));
        }
    }

    //Prints out the process list for testing
    public void showList()
    {
        for(int i = 0; i < processList.size(); i++) {
            System.out.println("ID: " + processList.get(i).getProcID() + " Size: " + processList.get(i).getSize());
        }
    }
}
