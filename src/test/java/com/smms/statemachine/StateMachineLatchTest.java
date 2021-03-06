package com.smms.statemachine;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 08/06/20
 * Time: 4:20 pm
 * email: priytam.pandey@cleartrip.com
 */
public class StateMachineLatchTest {

    @Test
    public void shouldBlock() throws Throwable {
        long time = System.currentTimeMillis();
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 2000);
        stateMachineLatch.block();
        Assert.assertTrue(System.currentTimeMillis() - time >= 2000);
    }

    @Test
    public void shouldExpire() throws Throwable {
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 2000);
        stateMachineLatch.block();
        Assert.assertTrue(stateMachineLatch.isExpired());
    }

    @Test
    public void shouldRelease() throws Throwable {
        StateMachineLatch stateMachineLatch = new StateMachineLatch("", 16000);
        new Thread( () -> {
            try {
                Thread.sleep(800);
                stateMachineLatch.release(null);
            } catch (Throwable ignored) {
            }
        }).start();
        long beforeBlock = System.currentTimeMillis();
        stateMachineLatch.block();
        long afterBlock = System.currentTimeMillis();
        Assert.assertTrue(afterBlock - beforeBlock < 16000);
    }

    @Test
    public void eventShouldBeSame() throws Throwable {
        StateMachineLatch stateMachineLatch = new StateMachineLatch("a", 2000);
        stateMachineLatch.block();
        Assert.assertEquals("a", stateMachineLatch.getEvent());
    }
}