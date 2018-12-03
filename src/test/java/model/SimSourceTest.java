package model;

import model.process.Process;
import model.process.SimSource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimSourceTest {

    Random rand;

    private int initId;

    private SimSource source;

    @Before
    public void beforeEach() {
        rand = new Random();
        this.initId = rand.nextInt() + 1;
        this.source = new SimSource(100,initId);
    }

    @Test
    public void testSimSource() {
        SimSource test = new SimSource(100,initId);
        assertEquals(initId, test.getId());
    }

    @Test
    public void testKill() {

        Process aliveProcess = source.generateProcess();
        int alivePid = aliveProcess.getProcId();

        this.source.kill(alivePid);

        boolean stillAlive = source.getAll().contains(aliveProcess);

        assertFalse(stillAlive);
    }

    @Test
    public void testGetAll() {

        final int numProc = 20;
        final int totalProc = source.getAll().size() + numProc;

        for (int i = 0; i < numProc; i++) {
            source.generateProcess();
        }

        assertEquals(totalProc, source.getAll().size());
    }

    @Test
    public void testGenerateProcess() {
        Process testProc = source.generateProcess();

        assertFalse(testProc.getBaseAddress().isPresent());
    }

    @Test
    public void testGetId() {
        SimSource test = new SimSource(100,1);
        assertEquals(1, test.getId());
    }
}

