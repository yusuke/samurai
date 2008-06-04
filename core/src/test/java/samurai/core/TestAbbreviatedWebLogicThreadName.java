package samurai.core;

import junit.framework.TestCase;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.0
 */
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
