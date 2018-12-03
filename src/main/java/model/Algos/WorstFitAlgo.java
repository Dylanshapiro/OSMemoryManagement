package model.Algos;

import java.util.Arrays;
import java.util.HashMap;

public class WorstFitAlgo implements Algo {

    @Override
    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem) {
        Object[] keys = freeMem.keySet().toArray();
        Arrays.sort(keys);
        Long largestAddress = null;
        int i;
        //assign largestAddress to first viable base address
        for (i = 0; i < keys.length; i++) {

            Long baseAddress = (Long) keys[i];
            Long freeSize = freeMem.get(baseAddress) - baseAddress;

            if (freeSize > procSize) {
                largestAddress = baseAddress;
                break;
            }
        }
        //check if largestAddress has been filled
        if (largestAddress == null) {
            return largestAddress;
        }
        //check for more viable chunks that could replace smallest address
        for (int j = i; j < keys.length; j++){

            Long baseAddress = (Long) keys[i];
            Long freeSize = freeMem.get(baseAddress) - baseAddress;
            if (freeSize > procSize && freeSize > largestAddress) {
                largestAddress = baseAddress;
                break;
            }
        }

        return largestAddress;
    }

    @Override
    public String getName() {
        return "Worst Fit";
    }
}
