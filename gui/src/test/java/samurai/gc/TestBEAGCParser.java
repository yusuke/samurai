package samurai.gc;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class TestBEAGCParser extends AbstractGraphTest{

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOldLog() throws Exception {
        BEAGCParser parser = new BEAGCParser();
        expected.add(new double[]{197.487d, 34347d, 26260d});
        expectedMax.add(141312d);
        expectedMax.add(141312d);
        parser.parse("[memory ] 14.210: Nursery GC 34347K->26260K (141312K), 197.487 ms", this);
        expected.add(new double[]{6388.739d, 125428d, 43203d});
        parser.parse("[memory ] 1437.757-1445.118: GC 125428K->43203K (141312K), 7.361 s (6388.739 ms)", this);
        expected.add(new double[]{6388.739d, 123428d, 43213d});
        parser.parse("[memory ] 1437.757-1445.118: GC 123428K->43213K (141312K), 7.361 s (6388.739 ms)", this);
        assertEquals(3, count);
    }

    public void testNewLog() throws Exception {
        BEAGCParser parser = new BEAGCParser();
        expected.add(new double[]{58.556d, 97781d, 37613d});
        expectedMax.add(98304d);
        expectedMax.add(98304d);
        parser.parse("[memory ] 72.250-72.390: GC 97781K->37613K (98304K), 58.556 ms", this);
        expected.add(new double[]{61.533d, 43810d, 37089d});
        parser.parse("[memory ] 72.453-72.562: GC 43810K->37089K (98304K), 61.533 ms", this);
        expected.add(new double[]{62.000d, 48748d, 38384d});
        parser.parse("[memory ] 72.625-72.687: GC 48748K->38384K (98304K), 62.000 ms", this);
        assertEquals(3, count);
    }
}
