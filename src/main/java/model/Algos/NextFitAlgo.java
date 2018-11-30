package model.Algos;

public class NextFitAlgo extends FitAlgo{
    private int count=0;
    public NextFitAlgo(int totalmem) {
        super(totalmem);
        name = "Next Fit";
    }

    @Override
    public Integer allocPs(long procsize) {
        int start = count;
        int open = 0;

        for(int i = count;i < memory.length; i=(i+1)%memory.length) {
            if(!memory[i]){
                open++;
                if(open >= procsize){
                    count=start+open;
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
