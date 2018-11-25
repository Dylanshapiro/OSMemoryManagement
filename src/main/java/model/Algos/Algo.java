package model.Algos;

import model.Process;

public interface Algo {

    public Integer allocPs(Process unallocated);
    public boolean deallocate(Process allocated);
    public String getName();
}
