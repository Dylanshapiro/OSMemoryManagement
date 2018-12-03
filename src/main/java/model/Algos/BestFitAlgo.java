package model.Algos;

import model.Process;

import java.util.Optional;

public class BestFitAlgo extends FitAlgo {
    public BestFitAlgo(long totalmem) {
        super(totalmem);
        name = "Best Fit";
    }

    @Override
    public Integer allocPs(long procSize) {
        Integer start=0,open=0,bestLength=Integer.MAX_VALUE,bestStart=null;
        for(int i=0;i<memory.length;i++){
            if(memory[i]||i==memory.length-1){
                if(open==procSize){
                    return start;
                }
                else if(open>procSize&&open<bestLength){
                    bestLength=open;
                    bestStart=start;
                }
                else{
                    open=0;
                    start=i+1;
                }
            }
            else{
                open++;
            }
        }
        if(bestStart!=null){
            return bestStart;
        }
        else{
            return null;
        }


    }
}
