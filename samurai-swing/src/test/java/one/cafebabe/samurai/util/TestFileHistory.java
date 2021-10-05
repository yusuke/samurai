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

import one.cafebabe.samurai.swing.FileHistory;
import one.cafebabe.samurai.swing.FileHistoryListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.CONCURRENT)
class TestFileHistory {

    @BeforeEach
    void setUp() {
        String name = "samuraitest";
        String fileName = System.getProperty("user.home") + File.separator + "." +
                name + ".properties";
        //noinspection ResultOfMethodCallIgnored
        new File(fileName).delete();
    }

    @Test
    void testOpenFIFO() {
        String name = "samuraitest";
        Configuration configuration = new Configuration(name);
        configuration.setInt("RecentlyUsedNumber", 4);
        FileHistory history = new FileHistory(configuration, new FileHistoryListener() {
            @Override
            public void fileOpened(File file) {
                
            }

            @Override
            public void filesOpened(File[] files) {

            }
        });
        history.clearHistory();
//        history.enableCleaningOrphans();
        File file1 = new File("src/main/resources/samurai/swing/close_hover.gif");
        File file2 = new File("src/main/resources/samurai/swing/close_push.gif");
        File file3 = new File("src/main/resources/samurai/swing/close.gif");
        File file4 = new File("src/main/resources/samurai/swing/css.vm");
        File file5 = new File("src/main/resources/samurai/swing/default.properties");
        history.open(file1);
        history.open(file2);
        history.open(file3);
        history.open(file4);
        history.open(file5);
        assertEquals(5, history.getList().size());
        assertEquals(file5, history.getList().get(0));
        assertEquals(file4, history.getList().get(1));
        assertEquals(file3, history.getList().get(2));
        assertEquals(file2, history.getList().get(3));
        assertEquals(file1, history.getList().get(4));
    }

    @Test
    void testValidation() {
        Configuration configuration = new Configuration("samuraitest");
        configuration.setInt("RecentlyUsedNumber", 4);
        FileHistory history = new FileHistory(configuration, new FileHistoryListener() {
            @Override
            public void fileOpened(File file) {
                
            }

            @Override
            public void filesOpened(File[] files) {

            }
        });
        history.clearHistory();
        history.enableCleaningOrphans();
        String base = new File("").getAbsolutePath().endsWith("samurai-swing") ? "" : "samurai-swing/";
        File file1 = new File(base + "src/main/resources/one/cafebabe/samurai/swing/images/close_hover.gif");
        File file2 = new File(base + "src/main/resources/one/cafebabe/samurai/swing/images/close_push.gif");
        File file3 = new File(base + "src/main/resources/one/cafebabe/samurai/swing/images/close.gif");
        File doesnotExist1 = new File(base + "foobarfoobar");
        File file4 = new File( "src/test/java/samurai/util/TestFileHistory.java");
        File doesnotExist2 = new File(base + "foobarfoobarbar");
        File directory1 = new File(base + "classes");
        File directory2 = new File(base + "src");
        File file5 = new File(base + "src/main/resources/one/cafebabe/samurai/util/default.properties");
        history.open(file1);
        history.open(file2);
        history.open(directory1);
        history.open(file3);
        history.open(directory2);
        history.open(file4);
        history.open(file5);
        history.open(doesnotExist2);
        history.open(doesnotExist1);
        assertEquals(4, history.getList().size());
        assertEquals(file5, history.getList().get(0));
        assertEquals(file3, history.getList().get(1));
        assertEquals(file2, history.getList().get(2));
        assertEquals(file1, history.getList().get(3));
    }
}

