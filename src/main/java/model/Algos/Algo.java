package model.Algos;

import model.Process;

public interface Algo {

    public Long allocPs(Process unallocated);
    public boolean deallocate(Process allocated);
    public String getName();
}
