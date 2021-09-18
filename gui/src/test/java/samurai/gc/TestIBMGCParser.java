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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class TestIBMGCParser extends AbstractGraphTest{

    @Test
    void testOldLog() {
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
