package model.Algos;

import java.util.Arrays;
import java.util.HashMap;

public class FirstFitAlgo implements Algo {


    @Override
    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem) {

        Object[] keys = freeMem.keySet().toArray();
        Arrays.sort(keys);

        for (int i = 0; i < keys.length; i++) {

            Long baseAddress = (Long) keys[i];

            if ((freeMem.get(baseAddress) - baseAddress) > procSize) {
                return baseAddress;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "First Fit";
    }
}
