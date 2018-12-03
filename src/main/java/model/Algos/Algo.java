package model.Algos;

import java.util.HashMap;

public interface Algo {

    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem);
    public String getName();
}
