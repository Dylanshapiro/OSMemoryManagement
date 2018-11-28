package model.Algos;

import model.Process;

import java.util.Optional;



public class FirstFitAlgo extends FitAlgo {


    //divides total memory to make into kilobytes.
    public FirstFitAlgo(int totalmem){
        super(totalmem);
        name = "First Fit";
    }

    // Implements Firstfit algorithm
    public Integer allocPs(long procsize){
        int start = 0;
        int open = 0;

        for(int i = 0;i < memory.length; i++) {
            if(!memory[i]){
               open++;
               if(open >= procsize){
                   return new Integer (start);
               }
            } else {
               open = 0;
               start = i+1;
            }
        }
        return null;
    }
}
