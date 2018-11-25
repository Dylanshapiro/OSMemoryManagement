package Model.Algos;

import Model.Process;

import java.util.Optional;

public class NextFitAlgo extends FitAlgo{
    private int count=0;
    public NextFitAlgo(int totalmem) {
        super(totalmem);
    }

    @Override
    public Integer allocPs(Process unallocated) {
        long procsize = getProcessSize(unallocated);
        int start = 0;
        int open = 0;

        for(int i = count;i < memory.length; i=(i+1)%memory.length) {
            if(!memory[i]){
                open++;
                if(open >= procsize){
                    filler(start, start+open,true);
                    count=start+open;
                    unallocated.setBaseAddress(start);
                    return new Integer (start);
                }
            } else {
                open = 0;
                start = i+1;
            }
            if(i==count-1){
                break;
            }
        }

        return null;
    }
}
