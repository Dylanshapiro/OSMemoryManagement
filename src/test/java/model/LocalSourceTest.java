package model;

import model.process.LocalSource;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class LocalSourceTest {


    private Random rand;

    private int initId;
    private LocalSource source;


    @Before
    public void beforeEach() {
        rand = new Random();
        initId = rand.nextInt();
        source = new LocalSource(initId);
    }


    @Test
    public void testGetAll(){
        assertTrue(source.getAll().size() >  0);
    }

}

