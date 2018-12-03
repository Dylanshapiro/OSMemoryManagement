package model.Algos;

import model.process.Process;

import java.util.List;

public interface Algo {

    public Long allocPs(Process unallocated);
    public boolean deallocate(Process allocated);
    public String getName();
    public void setRepresentation(List<Process> processes);
}
