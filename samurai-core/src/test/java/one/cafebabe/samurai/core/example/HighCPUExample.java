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

@SuppressWarnings({"InfiniteLoopStatement", "MismatchedQueryAndUpdateOfStringBuilder", "BusyWait"})
public class HighCPUExample {
    public static void main(String[] args) {

        class Load extends Thread {
            private final int threshold;

            public Load(String name, int threshold) {
                super(name);
                this.threshold = threshold;
            }

            @Override
            public void run() {
                StringBuilder buf = new StringBuilder();
                while (true) {
                    for (int i = 0; i < 1000; i++) {
                        buf.append(i);
                    }
                    buf.delete(0, buf.length());
                    if (threshold < 1000) {
                        long sleepDuration = System.currentTimeMillis() % 1000;
                        if (threshold < sleepDuration) {
                            try {
                                Thread.sleep(1000 - sleepDuration);
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                }
            }
        }
        for (int i = 1; i <= 10; i+=3) {
            Load load = new Load("load-" + i * 10 + "%", i * 100);
            load.start();
        }
    }
}
