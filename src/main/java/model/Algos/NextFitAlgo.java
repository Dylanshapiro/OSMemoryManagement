package model.Algos;

import java.util.Arrays;
import java.util.HashMap;

public class NextFitAlgo implements Algo{


    private static Long startBase;
    private static int startIndex = 0;

    @Override
    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem) {

        Object[] keys = freeMem.keySet().toArray();
        Arrays.sort(keys);

        if (startBase == null) {
            startBase = (long) 0;
        }
        //calculate start index
        //TODO figure out way to know what index in key array to start from

        //how many iterations
        int counter = 0;
        //first fit but start at startAddress
        for (int i = startIndex; counter < keys.length; i = (i + 1) % keys.length) {

            Long baseAddress = (Long) keys[i];

            if ((freeMem.get(baseAddress) - baseAddress) > procSize) {
                startBase = baseAddress + 1;
                return baseAddress;
            }
            counter ++;
        }
        return null;
    }

    @Override
    public String getName() {
        return "Next Fit";
    }
}
