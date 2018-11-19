package Model.Algos;

import Model.Process;

public interface Algo {

    public Integer allocPs(Process unallocated);
    public boolean deallocate(Process allocated);
}
