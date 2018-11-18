package Model;

import java.util.LinkedList;

public class FirstFitAlgo implements Algo {

    boolean[] memory;

    //divides total memory to make into kilobytes.
    public FirstFitAlgo(long totalmem){
        memory = new boolean[(int)totalmem/1024];
    }

    // Implements Firstfit algorithm
    public Long allocPs(Process unallocated){
        long procsize = unallocated.getSize();
        int start = 0;
        int open = 0;

        for(int i = 0;i <= memory.length; i++) {
            if(!memory[i]){
               open++;
               if(open >= procsize){
                   filler(start, open,true);
                   unallocated.setStart(start + 1);
                   return new Long (start + 1);
               }
            } else {
               open = 0;
               start = i;
            }
        }

        return null;
    }

    public boolean deallocate(Process allocated){
       int base = allocated.getStart();
       int size = allocated.getSize();

       filler(base-1,size,false);
       return true;
    }

    private void filler(int index, int size, boolean change){
        for(index+=1 ;index <= size ;index++){
            memory[index] = change;
        }
    }
}
