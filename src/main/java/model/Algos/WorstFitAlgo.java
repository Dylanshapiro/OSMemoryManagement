package model.Algos;

public class WorstFitAlgo extends FitAlgo {
    public WorstFitAlgo(long totalmem) {
        super(totalmem);
        name = "Worst Fit";
    }

    @Override
    public Integer allocPs(long procSize) {
        Integer start=0,open=0,worstLength=Integer.MIN_VALUE,worstStart=null;
        for(int i=0;i<memory.length;i++){
            if(memory[i]||i==memory.length-1){
                if(open==procSize){
                    return start;
                }
                else if(open>procSize&&open>worstLength){
                    worstLength=open;
                    worstStart=start;
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
        if(worstStart!=null){
            return worstStart;
        }
        else{
            return null;
        }
    }
}
