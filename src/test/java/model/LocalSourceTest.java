package model;

import model.process.LocalSource;
import model.process.Process;
import model.process.SimSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

