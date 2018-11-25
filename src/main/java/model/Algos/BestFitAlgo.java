package model.Algos;

import model.Process;

import java.util.Optional;

public class BestFitAlgo extends FitAlgo {
    public BestFitAlgo(int totalmem) {
        super(totalmem);
        name = "Best Fit";
    }

    @Override
    public Integer allocPs(Process unallocated) {
        long procSize=super.getProcessSize(unallocated);
        if(procSize>memory.length){
            return null;
        }
        int bestStart=-1;
        int bestLength=-1;
        int start=0;
        int length=0;
        for(int i=0;i<memory.length;i++){
            if(memory[i]){
                if(length==procSize){
                    unallocated.setBaseAddress(start);
                    filler(start,length,true);
                    return length;
                }
                if(length>procSize&&length<bestLength){
                    bestStart=start;
                    bestLength=length;
                }
                start=i+1;
                length=0;
            }
            else{
                length++;

            }
        }
        if(bestLength!=-1&&bestStart!=-1){
            unallocated.setBaseAddress(bestStart);
            filler(bestStart,(int)procSize,true);
            return bestStart;
        }
        return null;


    }
}
