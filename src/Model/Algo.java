package Model;

public interface Algo {

    public Integer allocPs(Process unallocated);
    public boolean deallocate(Process allocated);
}
