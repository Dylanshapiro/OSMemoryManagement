package Model;

import java.util.LinkedList;
import java.util.Optional;



public class FirstFitAlgo implements Algo {

    public static final int KILOBYTE = 1024;
    boolean[] memory;

    //divides total memory to make into kilobytes.
    public FirstFitAlgo(int totalmem){
        memory = new boolean[totalmem/KILOBYTE];
    }

    // Implements Firstfit algorithm
    public Integer allocPs(Process unallocated){
        long procsize = (unallocated.getSize() % KILOBYTE == 0 )
                ? unallocated.getSize()/ KILOBYTE: (unallocated.getSize()/KILOBYTE) + 1;


        int start = 0;
        int open = 0;

        for(int i = 0;i < memory.length; i++) {
            if(!memory[i]){
               open++;
               if(open >= procsize){
                   filler(start, start+open,true);
                   unallocated.setBaseAddress(Optional.of(new Integer(start)));
                   return new Integer (start);
               }
            } else {
               open = 0;
               start = i+1;
            }
        }

        return null;
    }

    public boolean deallocate(Process allocated){
       Optional<Integer> base = allocated.getBaseAddress();
       int size = allocated.getSize();

       filler(base.get(),size,false);
       return true;
    }

    private void filler(int index, int size, boolean change){
        for(;index < size ;index++){
            memory[index] = change;
        }
    }
}
