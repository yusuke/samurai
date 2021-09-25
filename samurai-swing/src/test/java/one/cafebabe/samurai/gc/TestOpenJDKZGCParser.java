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
class TestOpenJDKZGCParser extends AbstractGraphTest {

    @Test
    void zgc() throws IOException {
        OpenJDKZGCParser parser = new OpenJDKZGCParser();

        try (var br = new BufferedReader(
                new InputStreamReader(
                        TestOpenJDKZGCParser.class.
                                getResourceAsStream("/one/cafebabe/samurai/gc/jdk17-verbosegc-ZGC.log")))) {
            parser.parse(br.readLine(), this);
            parser.parse(br.readLine(), this);
//[1.190s][info][gc] GC(0) Garbage Collection (Warmup) 14M(11%)->6M(5%)
            expected.add(new double[]{
                    14d, 6d
            });
            expectedMax.add(127d);
            expectedMax.add(127d);
            parser.parse(br.readLine(), this);
//[2.816s][info][gc] GC(1) Garbage Collection (Warmup) 28M(22%)->28M(22%)
            expected.add(new double[]{
                    28d, 28d
            });
            parser.parse(br.readLine(), this);
            parser.parse(br.readLine(), this);
            parser.parse(br.readLine(), this);
            parser.parse(br.readLine(), this);

            assertEquals(2, count);

        }
    }


}
