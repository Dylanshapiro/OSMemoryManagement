package model;

import model.Algos.Algo;
import model.Algos.FirstFitAlgo;
import model.process.Process;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessTest {

    private static final int id = 2;
    private static final int startTime = 0;
    private static final long size = 4096;

    private static Process proc;

    // runs before EVERY test
    @Before
    public void beforeEach() {
        proc = new Process("testProcess", id, startTime, size);
    }

    @Test
    public void testGetSize() {
        assertEquals(size, proc.getSize().longValue());
    }

    @Test
    public void testSetSize() {
        final long newSize = 1024;

        proc.setSize(newSize);
        assertEquals(newSize, proc.getSize().longValue());
    }

    @Test
    public void testGetBaseAddressUnalloc() {
        assertFalse(proc.getBaseAddress().isPresent());
    }

    @Test
    public void testGetBaseAddressAlloc() {

        // Create an Algo and MemoryManager instace for test
        final MemoryManager tempMan = MemoryManager.getInstance();
        final Long defaultMemSize = tempMan.getMemSize();

        final Algo tempAlgo = new FirstFitAlgo(tempMan.getMemSize());

        tempMan.setAlgo(tempAlgo);

        // Allocate the Process
        tempMan.allocate(proc);

        // Check if it now has a base Address
        assertTrue(proc.getBaseAddress().isPresent());
    }

    @Test
    public void testSetBaseAddress() {
        Process tester = new Process("test", 1, 1, new Long(20));

        tester.setBaseAddress(new Long(20));

        assertEquals(new Long(20), tester.getBaseAddress().get());
    }

    @Test
    public void testGetStartTime() {
        assertEquals(startTime, proc.getStartTime());
    }

    @Test
    public void testSetStartTime() {
        final int difTime = 2;
        proc.setStartTime(difTime);

        assertNotEquals(startTime, proc.getStartTime());
    }

    @Test
    public void testGetProcId() {
        assertEquals(id,proc.getProcId());
    }

    @Test
    public void testSetProcId(){
        final int difId = 3;
        proc.setProcId(difId);

        assertNotEquals(id, proc.getProcId());
    }

}

