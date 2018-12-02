package model.Algos;

import model.process.Process;

public interface Algo {

    public boolean[] allocate (Process P);
    public Long allocPs(Process unallocated);
    public boolean[] deallocate(Process allocated);
    public void setMemoryState(boolean[] memState);
    public String getName();
}
