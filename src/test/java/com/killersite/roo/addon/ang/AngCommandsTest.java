package com.killersite.roo.addon.ang;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by Ben on 2/9/14.
 */
public class AngCommandsTest {

    private AngCommands commands;

    @Before
    public void setUp() throws Exception {
        commands = new AngCommands();
        commands.angOperations = mock(AngOperations.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testIsCommandAvailable() throws Exception {

    }

    @Test
    public void testAll() throws Exception {

    }

    @Test
    public void testSetup() throws Exception {

    }
}
