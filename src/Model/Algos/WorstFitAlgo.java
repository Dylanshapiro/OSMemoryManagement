package Model.Algos;

import Model.Process;

import java.util.Optional;

public class WorstFitAlgo extends FitAlgo {
    public WorstFitAlgo(int totalmem) {
        super(totalmem);
    }

    @Override
    public Integer allocPs(Process unallocated) {
        long procSize=super.getProcessSize(unallocated);
        if(procSize>memory.length){
            return null;
        }
        int worstStart=-1;
        int worstLength=-1;
        int start=0;
        int length=0;
        for(int i=0;i<memory.length;i++){
            if(memory[i]){
                if(length>procSize&&length>worstLength){
                    worstStart=start;
                    worstLength=length;
                }
                start=i+1;
                length=0;
            }
            else{
                length++;

            }
        }
        if(worstLength!=-1&&worstStart!=-1){
            unallocated.setBaseAddress(Optional.of(new Integer(worstStart)));
            filler(worstStart,(int)procSize,true);
            return worstStart;
        }
        return null;
    }
}
