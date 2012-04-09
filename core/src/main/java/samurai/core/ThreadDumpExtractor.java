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
package samurai.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


public class ThreadDumpExtractor {
    private ThreadDumpRenderer renderer;

    public ThreadDumpExtractor(ThreadDumpRenderer renderer) {
        this.renderer = renderer;
    }

    public void analyze(String threadDump) {
        StringTokenizer tokenizer = new StringTokenizer(threadDump, "\n");
        while (tokenizer.hasMoreTokens()) {
            analyzeLine(tokenizer.nextToken());
        }
        finish();
    }

    private boolean whileFullThreadDump = false;
    private boolean whileAthreadDump = false;
    FullThreadDump fullThreadDump = null;
    ThreadDump aThreadDump = null;

    /**
     * Extracts thread dumps from InputStream and closes it.
     * @param is - the underlying input stream
     * @throws IOException - If an I/O error occurs
     */
    public void analyze(InputStream is) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while (null != (line = reader.readLine())) {
                analyzeLine(line);
            }
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }
        finish();
    }

    /**
     * Extracts thread dumps from the specified file
     * @param file - the file to be examined
     * @throws IOException - If an I/O error occurs
     */
    public void analyze(File file) throws IOException {
        analyze(new FileInputStream(file));
    }

    private boolean whileIBMlockInfo = false;
    private IBMLockInfos ibmLockInfo = null;

    public void analyzeLine(String line) {
        checkBeginFullThreadDump(line);
        if (!whileFullThreadDump && line.startsWith(IBMLockInfos.HEADER)) {
            whileIBMlockInfo = true;
            ibmLockInfo = new IBMLockInfos(line);
        } else if (whileIBMlockInfo) {
            if (line.startsWith(IBMLockInfos.FOOTER)) {
                whileIBMlockInfo = false;
            } else {
                ibmLockInfo.addLine(line);
            }
        }

        if (whileFullThreadDump) {
            if (!fullThreadDump.isThreadDumpContinuing(line)) {
                finish();
//        fullThreadDumpEnded();
            } else {
                if (fullThreadDump.isThreadHeader(line)) {
                    if (whileAthreadDump) {
                        //new dump found
                        aThreadDumpEnded();
                    }
                    aThreadDumpStarted(line);
                } else if (whileAthreadDump) {
                    if (!fullThreadDump.isThreadFooter(line)) {
                        aThreadDump.addStackLine(line);
                    } else {
                        aThreadDumpEnded();
                    }
                }
            }
        }
    }

    private final int SUN = 0;
    private final int BEA = 1;
    private final int IBM = 2;

    private final String[] FULL_THREAD_DUMP_HEADER = {
            //SUN
            "Full thread dump"
            //BEA
            , "===== FULL THREAD DUMP ==============="
            //IBM
            , "2XMFULLTHDDUMP"};


    private int currentJVM = SUN;

//  private boolean isBEAThreadDump = false;

    //  private int threadDumpIndex = 0;
    private void checkBeginFullThreadDump(String line) {
        for (int i = 0; i < FULL_THREAD_DUMP_HEADER.length; i++) {
            if (line.startsWith(FULL_THREAD_DUMP_HEADER[i])) {
                currentJVM = i;
                if (whileAthreadDump) {
                    aThreadDumpEnded();
                }
                if (whileFullThreadDump) {
                    fullThreadDumpEnded();
                }
                fullThreadDumpStarted(line);
                break;
            }

        }

    }

    public void finish() {
        if (whileAthreadDump) {
            aThreadDumpEnded();
        }
        if (whileFullThreadDump) {
            fullThreadDumpEnded();
        }
    }

    private void fullThreadDumpStarted(String header) {
        switch (this.currentJVM) {
            case SUN:
                fullThreadDump = new SunFullThreadDump(header);
                break;
            case BEA:
                fullThreadDump = new BEAFullThreadDump(header);
                break;
            case IBM:
                fullThreadDump = new IBMFullThreadDump(header);
                break;
        }
        whileFullThreadDump = true;
    }

    private void fullThreadDumpEnded() {
        whileFullThreadDump = false;
        if (null != fullThreadDump) {
            fullThreadDump.finish();
        }
        renderer.onFullThreadDump(fullThreadDump);
    }

    private void aThreadDumpStarted(String header) {
        switch (currentJVM) {
            case SUN:
                aThreadDump = new SunThreadDump(header);
                break;
            case BEA:
                aThreadDump = new BEAThreadDump(header);
                break;
            case IBM:
                aThreadDump = new IBMThreadDump(header, ibmLockInfo);
                break;
            default:
                throw new AssertionError("Illegal JVM Vendor code");
        }
        whileAthreadDump = true;
    }

    private void aThreadDumpEnded() {
        fullThreadDump.addThreadDump(aThreadDump);
        whileAthreadDump = false;
        renderer.onThreadDump(aThreadDump);
    }
}
