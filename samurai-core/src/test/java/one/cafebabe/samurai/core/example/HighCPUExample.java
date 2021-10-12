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
package one.cafebabe.samurai.core.example;

@SuppressWarnings({"InfiniteLoopStatement", "MismatchedQueryAndUpdateOfStringBuilder", "StringRepeatCanBeUsed", "BusyWait"})
public class HighCPUExample {
    public static void main(String[] args) {
        Thread highCPU = new Thread("high-cpu") {
            @Override
            public void run() {
                while (true) {
                    StringBuilder buf = new StringBuilder();
                    for (int i = 0; i < 1000; i++) {
                        buf.append("hello world");
                    }
                    buf.delete(0, buf.length());
                }
            }
        };

        highCPU.start();
        Thread lowCPU = new Thread("low-cpu") {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        };
        lowCPU.start();
    }
}
