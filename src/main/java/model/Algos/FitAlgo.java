package model.Algos;

import model.process.Process;

public abstract class FitAlgo implements Algo{
    public static final int KILOBYTE = 1024;
    protected boolean[] memory;
    protected String name;

    public FitAlgo(int totalmem){
        memory = new boolean[totalmem/KILOBYTE];
    }
    protected void filler(int index, long size, boolean change){
        for(int i=0;i < size ;i++){
            memory[index+i] = change;
        }
    }
    public boolean[] deallocate(Process allocated){
        int base = (int)allocated.getBaseAddress().get().longValue()/1024;
        long size = getProcessSize(allocated);

        filler(base,size,false);
        return memory;
    }
    protected long getProcessSize(Process unallocated){
        return (unallocated.getSize() % KILOBYTE == 0 )
                ? unallocated.getSize()/ KILOBYTE: (unallocated.getSize()/KILOBYTE) + 1;
    }

    public String getName(){
        return name;
    }

    @Override
    public boolean[] allocate(Process P) {

        if (allocPs(P) != null) {
            return memory;
        }

        return null;
    }

    @Override
    public void setMemoryState(boolean[] memState) {

        memory = memState;

    }

    @Override
    public Long allocPs(Process unallocated) {
        long procsize = getProcessSize(unallocated);
        Integer start=allocPs(procsize);
        if(start==null){
            return null;
        }
        filler(start,procsize,true);
        unallocated.setBaseAddress(new Long(start*KILOBYTE));
        return new Long(start*KILOBYTE);
    }

    /**
     *
     * @param size the sizeof the process in megabytes
     * @return a base address for the process
     */
    protected abstract Integer allocPs(long size);
}
