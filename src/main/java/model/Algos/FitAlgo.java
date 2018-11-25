package model.Algos;

import model.Process;

import java.util.Optional;

public abstract class FitAlgo implements Algo{
    public static final int KILOBYTE = 1024;
    protected boolean[] memory;
    protected String name;

    public FitAlgo(int totalmem){
        memory = new boolean[totalmem/KILOBYTE];
    }
    protected void filler(int index, int size, boolean change){
        for(;index < size ;index++){
            memory[index] = change;
        }
    }
    public boolean deallocate(Process allocated){
        Optional<Integer> base = allocated.getBaseAddress();
        int size = allocated.getSize();

        filler(base.get(),size,false);
        return true;
    }
    protected long getProcessSize(Process unallocated){
        return (unallocated.getSize() % KILOBYTE == 0 )
                ? unallocated.getSize()/ KILOBYTE: (unallocated.getSize()/KILOBYTE) + 1;
    }

    public String getName(){
        return name;
    }
}
