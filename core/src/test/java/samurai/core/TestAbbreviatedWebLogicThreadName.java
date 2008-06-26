/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

import junit.framework.TestCase;

public class TestAbbreviatedWebLogicThreadName extends TestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAbbreviatedWebLogicThreadName.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAbbreviateWebLogicThreadName() {
        assertEquals("default[0]", ThreadDumpSequence.abbreviateWebLogicThreadName("ExecuteThread: '0' for queue: 'default'"));
        assertEquals("weblogic.kernel.Default[3]", ThreadDumpSequence.abbreviateWebLogicThreadName("[STANDBY] ExecuteThread: '3' for queue: 'weblogic.kernel.Default (self-tuning)'"));
        assertEquals("weblogic.kernel.Default[2]", ThreadDumpSequence.abbreviateWebLogicThreadName("[ACTIVE] ExecuteThread: '2' for queue: 'weblogic.kernel.Default (self-tuning)'"));
    }

}
