package Model.Algos;

import Model.Process;

import java.util.Optional;



public class FirstFitAlgo extends FitAlgo {


    //divides total memory to make into kilobytes.
    public FirstFitAlgo(int totalmem){
        super(totalmem);
    }

    // Implements Firstfit algorithm
    public Integer allocPs(Process unallocated){
        long procsize = getProcessSize(unallocated);
        int start = 0;
        int open = 0;

        for(int i = 0;i < memory.length; i++) {
            if(!memory[i]){
               open++;
               if(open >= procsize){
                   filler(start, start+open,true);
                   unallocated.setBaseAddress(start);
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
