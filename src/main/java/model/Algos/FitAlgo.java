package model.Algos;

import model.process.Process;

import java.util.List;

public abstract class FitAlgo implements Algo{
    public static long BlockSize;
    protected boolean[] memory;
    protected String name;
    private long totalMem;
    public FitAlgo(long totalmem){
        BlockSize=Math.toIntExact(totalmem/ 1048576L);
        this.totalMem=totalmem;
        memory = new boolean[1048576];
    }
    protected void filler(int index, long size, boolean change){
        for(int i=0;i < size ;i++){
            memory[index+i] = change;
        }
    }
    public boolean deallocate(Process allocated){
        int base = (int)(allocated.getBaseAddress().get().longValue()/BlockSize);
        long size = getProcessSize(allocated);

        filler(base,size,false);
        return true;
    }
    protected long getProcessSize(Process unallocated){
        return (unallocated.getSize() % BlockSize == 0 )
                ? unallocated.getSize()/ BlockSize : (unallocated.getSize()/ BlockSize) + 1;
    }

    public String getName(){
        return name;
    }

    @Override
    public Long allocPs(Process unallocated) {
        long procsize = getProcessSize(unallocated);
        Integer start=allocPs(procsize);
        if(start==null){
            return null;
        }
        filler(start,procsize,true);
        unallocated.setBaseAddress(new Long(((long)start)* BlockSize));
        return new Long(((long)start)* BlockSize);
    }
    public void setRepresentation(List<Process> processes)
    {
        memory = new boolean[memory.length];
        for(Process p:processes){
            if(p.getBaseAddress().get().longValue()/BlockSize +getProcessSize(p)<memory.length)
                filler((int)(p.getBaseAddress().get().longValue()/ BlockSize),getProcessSize(p),true);
        }
    }

    @Override
    public void setMemSize(long memSize) {
        BlockSize= memSize/ 1048576L;
        this.totalMem=memSize;
    }

    /**
     *
     * @param size the sizeof the process in megabytes
     * @return a base address for the process
     */
    protected abstract Integer allocPs(long size);
}
