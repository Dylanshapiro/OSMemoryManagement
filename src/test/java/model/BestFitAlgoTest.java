package model;

import model.Algos.BestFitAlgo;
import model.process.LocalSource;
import model.process.Process;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BestFitAlgoTest {

    private int memSize;
    private Random rand;
    BestFitAlgo algo;

    @Before
    public void beforeEach() {
        this.memSize = rand.nextInt(600000) + 1;
        this.algo = new BestFitAlgo(this.memSize);
    }

}

