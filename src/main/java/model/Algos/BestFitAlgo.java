package model.Algos;


import java.util.Arrays;
import java.util.HashMap;

public class BestFitAlgo implements Algo {


    @Override
    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem) {

        Object[] keys = freeMem.keySet().toArray();
        Arrays.sort(keys);
        Long smallestAddress = null;
        int i;
        //assign smallestAddress to first viable base address
        for (i = 0; i < keys.length; i++) {

            Long baseAddress = (Long) keys[i];
            Long freeSize = freeMem.get(baseAddress) - baseAddress;

            if (freeSize > procSize) {
                smallestAddress = baseAddress;
                break;
            }
        }
        //check if smallestAddress has been filled
        if (smallestAddress == null) {
            return smallestAddress;
        }
        //check for more viable chunks that could replace smallest address
        for (int j = i; j < keys.length; j++){

            Long baseAddress = (Long) keys[i];
            Long freeSize = freeMem.get(baseAddress) - baseAddress;
            if (freeSize > procSize && freeSize < smallestAddress) {
                smallestAddress = baseAddress;
                break;
            }
        }

        return smallestAddress;
    }

    @Override
    public String getName() {

        return "Best Fit";
    }
}
