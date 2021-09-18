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
package one.cafebabe.samurai.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
class TestSearcher  {
 
    String target = "a b c a";

    @Test
    void testSearchNext() {
//    SearchPanel searcher = new SearchPanel();
//    SearchResult result;
//    result = searcher.searchNextRegexp(target,"a",0);
//    assertTrue(result.found());
//    assertEquals(0,result.getStart());
//    assertEquals(1,result.getEnd());
//    result = searcher.searchNextRegexp(target,"a",1);
//    assertTrue(result.found());
//    assertEquals(6,result.getStart());
//    assertEquals(7,result.getEnd());
    }

    @Test void testSearchPrevious() {
//    SearchPanel searcher = new SearchPanel();
//    SearchResult result;
//    result = searcher.searchPreviousRegexp(target,"a",10);
//    assertTrue(result.found());
//    assertEquals(6,result.getStart());
//    assertEquals(7,result.getEnd());
//    result = searcher.searchPreviousRegexp(target,"a",5);
//    assertTrue(result.found());
//    assertEquals(0,result.getStart());
//    assertEquals(1,result.getEnd());
    }


}

