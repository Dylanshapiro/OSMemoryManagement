package model;

import model.Algos.BestFitAlgo;
import org.junit.Before;

import java.util.Random;

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

