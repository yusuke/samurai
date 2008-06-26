/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.gc;

public class TestIBMGCParser extends AbstractGraphTest{

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOldLog() throws Exception {
        IBMGCParser parser = new IBMGCParser();
        expected.add(new double[]{88d, 38304336d, 38821800d});
        expectedMax.add(73923072d);
        expectedMax.add(73923072d);
        parser.parse("  <GC(74): freed 38304336 bytes, 52% free (38821800/73923072), in 88 ms>", this);
        expected.add(new double[]{92d, 37702960d, 38146496d});
        parser.parse("  <GC(75): freed 37702960 bytes, 51% free (38146496/73923072), in 92 ms>", this);
        expected.add(new double[]{89d, 16412784, 38016120d});
        parser.parse("  <GC(76): freed 16412784 bytes, 51% free (38016120/73923072), in 89 ms>", this);
        assertEquals(3, count);
    }
}
