/*
 * Copyright 2003-2012 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samurai.gc;

public class TestSunGCParser extends AbstractGraphTest{

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testStandardVerbosegc() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{0.0254331d, 115008d, 103309d});
        expectedMax.add(129664d);
        expectedMax.add(129664d);
        assertTrue(parser.parse("[GC 115008K->103309K(129664K), 0.0254331 secs]", this));
        expected.add(new double[]{0.0328756d, 114957d, 105199d});
        assertTrue(parser.parse("[GC 114957K->105199K(129664K), 0.0328756 secs]", this));

        expectedMax.add(329551d);
        expectedMax.add(329551d);
        expected.add(new double[]{5.4551339d, 116847d, 48717d});
        assertFalse(parser.parse("[Full GC[Unloading class sun.reflect.GeneratedConstructorAccessor74]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._common._jsp.__403]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__comments]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedMethodAccessor224]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._styles.__global_css]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor78]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__trackbacks]", this));
        assertFalse(parser.parse("[Unloading class jsp_servlet._themes._custom._jsp.__blogentries]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor69]", this));
        assertFalse(parser.parse("[Unloading class sun.reflect.GeneratedConstructorAccessor75]", this));
        assertTrue(parser.parse(" 116847K->48717K(329551K), 5.4551339 secs]", this));

        expected.add(new double[]{1.0320474d, 61365d, 51414d});
        assertTrue(parser.parse("[Full GC 61365K->51414K(129664K), 1.0320474 secs]", this));
        expected.add(new double[]{0.0320474d, 60365d, 50414d});
        assertTrue(parser.parse("[GC 60365K->50414K(129664K), 0.0320474 secs]", this));
        parser.parse("", this);
        assertEquals(5, count);
    }
    public void testUseParNewGC() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{.1106169d, 214067d, 16986d});
        expectedMax.add(1022400d);
        expectedMax.add(1022400d);
        assertTrue(parser.parse("[ParNew 214067K->16986K(1022400K), 0.1106169 secs]", this));
        expected.add(new double[]{.3251635d, 226778d, 33381d});
        assertTrue(parser.parse("[ParNew 226778K->33381K(1022400K), 0.3251635 secs]", this));
        assertEquals(2, count);
    }

    public void testCMSWithPrintGCTimeStamps() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{0.1580060d, 0d, 6086d});
        expectedMax.add(3145728d);
        expectedMax.add(3145728d);
        expected.add(new double[]{13871d,13854d});
        expectedMax.add(131072d);
        expectedMax.add(131072d);
        assertTrue(parser.parse("3.202: [Full GC 3.202: [CMS: 0K->6086K(3145728K), 0.1578300 secs] 301994K->6086K(5976896K), [CMS Perm : 13871K->13854K(131072K)], 0.1580060 secs]", this));
        assertEquals(2, count);

        expected.add(new double[]{0.2179840d, 2516608d, 104213d});
        expectedMax.add(2831168d);
        expectedMax.add(2831168d);
        assertTrue(parser.parse("34.966: [GC 34.966: [ParNew: 2516608K->104213K(2831168K), 0.2179840 secs] 2522694K->110300K(5976896K), 0.2181880 secs]", this));
        assertEquals(3, count);

        expected.add(new double[]{0.2045160d, 2620821d, 129482d});
        assertTrue(parser.parse("374.638: [GC 374.638: [ParNew: 2620821K->129482K(2831168K), 0.2045160 secs] 2626908K->135568K(5976896K), 0.2046990 secs]", this));
        assertEquals(4, count);

        expected.add(new double[]{0.9489080d, 142151d, 182754d});
        expected.add(new double[]{131071d,92812d});
        expectedMax.add(3145728d);
        assertTrue(parser.parse("12656.259: [Full GC 12656.259: [CMS (concurrent mode failure): 142151K->182754K(3145728K), 0.9486870 secs] 2099331K->182754K(5976896K), [CMS Perm : 131071K->92812K(131072K)], 0.9489080 secs]", this));

    }

    public void testPrintGCTimeStamps() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{.1489631d, 79733d, 4275d});
        expectedMax.add(1022400d);
        expectedMax.add(1022400d);
        assertTrue(parser.parse("8.861: [Full GC 79733K->4275K(1022400K), 0.1489631 secs]", this));
        expected.add(new double[]{.3251635d, 226778d, 33381d});
        assertTrue(parser.parse("42.062: [ParNew 226778K->33381K(1022400K), 0.3251635 secs]", this));
        expected.add(new double[]{.3251635d, 226778d, 33381d});
        assertTrue(parser.parse("42.062: [GC 226778K->33381K(1022400K), 0.3251635 secs]", this));
        assertEquals(3, count);
    }
    public void testParallelGCDetails() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{0.0210314d, 25019d, 4285d});
        expectedMax.add(32384d);
        expectedMax.add(32384d);
        assertTrue(parser.parse("[GC [PSYoungGen: 25019K->4285K(32384K)] 49755K->29433K(62656K), 0.0210314 secs]", this));
        expected.add(new double[]{0.0188023d, 28733d, 1128d});
        assertTrue(parser.parse("[GC [PSYoungGen: 28733K->1128K(32000K)] 53881K->29949K(62272K), 0.0188023 secs]", this));
        expected.add(new double[]{0.2284255d, 1128d, 0d});
        expected.add(new double[]{0.2284255d, 28821d, 21467d});
        expected.add(new double[]{11772d, 11755d});
        expectedMax.add(66176d);
        expectedMax.add(66176d);
        expectedMax.add(23552d);
        expectedMax.add(23552d);
        assertTrue(parser.parse("[Full GC [PSYoungGen: 1128K->0K(32000K)] [ParOldGen: 28821K->21467K(66176K)] 29949K->21467K(98176K) [PSPermGen: 11772K->11755K(23552K)], 0.2284255 secs]", this));
        expected.add(new double[]{.3924338d, 7868d, 0d});
        expected.add(new double[]{.3924338d, 65738d, 36992d});
        expected.add(new double[]{11759d, 11758d});
        expectedMax.add(99072d);
        expectedMax.add(99072d);
        expectedMax.add(26624d);
        expectedMax.add(26624d);
        assertTrue(parser.parse("[Full GC [PSYoungGen: 7868K->0K(23488K)] [ParOldGen: 65738K->36992K(99072K)] 73606K->36992K(122560K) [PSPermGen: 11759K->11758K(26624K)], 0.3924338 secs]", this));
        assertEquals(8, count);
    }


    public void testPrintGCDetails() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{0.1165657d, 0d, 4271d});
        expected.add(new double[]{9991d, 9996d});
        expectedMax.add(786432d);
        expectedMax.add(786432d);
        expectedMax.add(131072d);
        expectedMax.add(131072d);
        assertTrue(parser.parse("[Full GC [Tenured: 0K->4271K(786432K), 0.1163157 secs] 79759K->4271K(1022400K), [Perm : 9991K->9996K(131072K)], 0.1165657 secs]", this));
        assertEquals(2, count);

        expected.add(new double[]{0.1117439d, 0d, 4271d});
        expected.add(new double[]{9999d, 9876d});
        assertTrue(parser.parse("8.017: [Full GC 8.017: [Tenured: 0K->4271K(786432K), 0.1114785 secs] 79759K->4271K(1022400K), [Perm : 9999K->9876K(131072K)], 0.1117439 secs]", this));
        assertEquals(4, count);

        expected.add(new double[]{0.1641700d, 0d, 4275d});
        expected.add(new double[]{9993d, 9982d});
        assertTrue(parser.parse("[Full GC [CMS: 0K->4275K(786432K), 0.1640437 secs] 89008K->4275K(1048384K), [CMS Perm : 9993K->9982K(131072K)], 0.1641700 secs]", this));
        assertEquals(6, count);

        expected.add(new double[]{0.1936041d, 0d, 4273d});
        expected.add(new double[]{9997d, 9986d});
        assertTrue(parser.parse("7.675: [Full GC 7.675: [CMS: 0K->4273K(786432K), 0.1872279 secs] 89008K->4273K(1048384K), [CMS Perm : 9997K->9986K(131072K)], 0.1936041 secs]", this));
        assertEquals(8, count);

        expected.add(new double[]{0.0784815d, 209792d, 12709d});
        expectedMax.add(235968d);
        expectedMax.add(235968d);
        assertTrue(parser.parse("23.266: [GC 23.266: [ParNew: 209792K->12709K(235968K), 0.0784815 secs] 214067K->16985K(1022400K), 0.0785944 secs]", this));
        assertEquals(9, count);

        expected.add(new double[]{0.1604727d, 222501d, 26176d});
        expectedMax.add(2359608d);
        expectedMax.add(2359608d);
        assertTrue(parser.parse("37.225: [GC 37.225: [ParNew: 222501K->26176K(2359608K), 0.1604727 secs] 226777K->33265K(1022400K), 0.1605807 secs]", this));
        assertEquals(10, count);

    }

    /**
     [GC [DefNew: 512K->64K(576K), 0.0023976 secs] 512K->316K(1984K), 0.0024659 secs]
     [GC [DefNew: 572K->64K(576K), 0.0019750 secs] 825K->472K(1984K), 0.0020216 secs]
     [GC [DefNew: 576K->64K(576K), 0.0015165 secs] 984K->687K(1984K), 0.0015580 secs]
     [GC [DefNew: 545K->64K(576K), 0.0017274 secs] 1169K->899K(1984K), 0.0017770 secs]
     [GC [DefNew: 576K->63K(576K), 0.0010807 secs] 1411K->1012K(1984K), 0.0011258 secs]
     [GC [DefNew: 575K->64K(576K), 0.0025559 secs] 1524K->1182K(1984K), 0.0026065 secs]
     [GC [DefNew: 544K->63K(576K), 0.0030917 secs] 1663K->1375K(1984K), 0.0031511 secs]
     [GC [DefNew: 538K->64K(576K), 0.0014637 secs][Tenured: 1427K->1074K(1536K), 0.0200039 secs] 1849K->1074K(2112K), 0.0215485 secs]
     [Full GC [DefNew: 1107K->128K(1152K), 0.0034856 secs] 13000K->12163K(15636K), 0.0035686 secs]

     */
    public void testPrintGCDetails2() throws Exception {
        SunGCParser parser = new SunGCParser();
        expected.add(new double[]{0.0023976d, 512d, 64d});
        expectedMax.add(576d);
        expectedMax.add(576d);
        assertTrue(parser.parse("[GC [DefNew: 512K->64K(576K), 0.0023976 secs] 512K->316K(1984K), 0.0024659 secs]", this));
        expected.add(new double[]{0.0014637d, 538d, 64d});
        expected.add(new double[]{0.0215485d, 1427d, 1074d});
        expectedMax.add(1536d);
        expectedMax.add(1536d);
        assertTrue(parser.parse("[GC [DefNew: 538K->64K(576K), 0.0014637 secs][Tenured: 1427K->1074K(1536K), 0.0200039 secs] 1849K->1074K(2112K), 0.0215485 secs]", this));
        expected.add(new double[]{0.0034856d, 1107d, 128d});
        expectedMax.add(1152d);
        expectedMax.add(1152d);
        assertTrue(parser.parse("[Full GC [DefNew: 1107K->128K(1152K), 0.0034856 secs] 13000K->12163K(15636K), 0.0035686 secs]", this));
    }
}
