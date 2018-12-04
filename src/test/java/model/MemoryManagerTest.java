package model;

import model.Algos.FirstFitAlgo;
import model.process.Process;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MemoryManagerTest {


    @Test
    public void testGetInstance() {
        MemoryManager first = MemoryManager.getInstance();
        MemoryManager another = MemoryManager.getInstance();

        assertTrue(first == another);
    }

    @Test
    public void testMemSize() {
        MemoryManager tester = MemoryManager.getInstance();

        final int testSize = 2123123;

        tester.setMemSize(testSize);

        assertEquals(testSize, tester.getMemSize());
    }

    @Test
    public void testClearProc() {
        Process testProc = new Process("test", 1, 1, new Long(10));
        MemoryManager tester = MemoryManager.getInstance();
        tester.allocate(testProc);
        tester.clearProc();
        assertEquals(0, tester.getAllProc().size());
    }
}
