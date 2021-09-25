/*
 * Copyright 2021 Yusuke Yamamoto
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
package one.cafebabe.samurai.gc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("ConstantConditions")
@Execution(ExecutionMode.CONCURRENT)
class TestOpenJDKParallelGCParser extends AbstractGraphTest {

    @Test
    void parallelGC() throws IOException {
        OpenJDKParallelGCParser parser = new OpenJDKParallelGCParser();

        try (var br = new BufferedReader(
                new InputStreamReader(
                        TestOpenJDKParallelGCParser.class.
                                getResourceAsStream("/one/cafebabe/samurai/gc/jdk11-verbosegc-UseParallelGC.log")))) {
            parser.parse(br.readLine(), this);
            parser.parse(br.readLine(), this);
//[2.226s][info][gc] GC(0) Pause Young (Metadata GC Threshold) 22M->4M(123M) 5.476ms
            expected.add(new double[]{
                    5.476d, 22d, 4d
            });
            expectedMax.add(123d);
            expectedMax.add(123d);
            parser.parse(br.readLine(), this);

//[2.246s][info][gc] GC(1) Pause Full (Metadata GC Threshold) 4M->4M(123M) 19.712ms
            expected.add(new double[]{
                    19.712d, 4d, 4d
            });
            parser.parse(br.readLine(), this);

            assertEquals(2, count);

        }
    }

}
