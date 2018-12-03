package model.Algos;

import model.MemoryManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Map.Entry;

public class NextFitAlgo implements Algo {

    private static Long lastAllocatedAt = new Long(0);

    @Override
    public Long allocateP(Long procSize, HashMap<Long, Long> freeMem) {
        System.out.println("totalMem: " + MemoryManager.getInstance().getMemSize());
        System.out.println("lastAllocated at: " + lastAllocatedAt);

        Optional<Entry<Long, Long>> maybeBigEnough = trySearch(procSize, freeMem, lastAllocatedAt);

        // if we couldnt find a hole check vals that werent checked yet
        if (!maybeBigEnough.isPresent()) {
            maybeBigEnough = trySearch(procSize, freeMem, new Long(0));
        }

        // All vals have now been checked (including those before starting point )
        // either a spot was found and it can be returned or there isnt one available
        if(!maybeBigEnough.isPresent()){
            System.out.println("not enough space to allocate");
            return null;
        } else {
            Entry<Long, Long> bigEnough = maybeBigEnough.get();
            lastAllocatedAt = bigEnough.getKey();
            System.out.println("allocating at : " +  bigEnough.getKey());
            return bigEnough.getKey();
        }
    }

    private Optional<Entry<Long, Long>> trySearch(Long procSize, HashMap<Long, Long> freeMem, Long startPoint) {
        return freeMem.entrySet()
                .stream()
                .sorted((a, b) -> Long.valueOf(a.getKey()).compareTo(Long.valueOf(b.getKey()))) // sort entries by base
                .filter(entry -> entry.getKey() >= startPoint) // throw away entries with base < last allocated
                .filter(entry -> entry.getValue() > procSize) //throw away entries cant fit the proc
                .findFirst(); // will get first proc that is left if there is one
    }


    @Override
    public String getName() {
        return "Next Fit";
    }
}
