package model;

import model.Algos.FirstFitAlgo;
import model.process.Process;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MemoryManagerTest {

    private Random rand;
    private Process testProc;
    private MemoryManager manager;

    @Before
    public void beforeEach(){
        final int memSize = 200000;

        Process testProc = new Process("test",
               10,
                10,
                new Long(10));

        this.rand = new Random();
        this.manager = MemoryManager.getInstance();
        this.manager.setMemSize(memSize);
        this.manager.setAlgo(new FirstFitAlgo(memSize));
    }

    @Test
    public void testGetInstance(){
        MemoryManager another = MemoryManager.getInstance();

        assertTrue(this.manager == another);
    }

    @Test
    public void testMemSize(){

        final int testSize = 2123123;

        this.manager.setMemSize(testSize);

        assertEquals(testSize, this.manager.getMemSize());
    }

    @Test(expected = NullPointerException.class)
    public void testClearProc(){
        this.manager.allocate(testProc);
        this.manager.clearProc();
        this.manager.getProcess(testProc.getProcId());
    }

}
