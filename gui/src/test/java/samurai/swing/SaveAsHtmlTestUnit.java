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
package samurai.swing;

import org.junit.jupiter.api.Test;
import samurai.core.ThreadStatistic;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaveAsHtmlTestUnit  {
    ThreadStatistic statistic = new ThreadStatistic();


    @Test
    void testFileName() {
        File file = new File("index.html");
        File target = ThreadDumpPanel.getTargetDirectory(file);
        assertEquals("index", target.getName());

        file = new File("src");
        target = ThreadDumpPanel.getTargetDirectory(file);
        assertEquals("src.1", target.getName());

    }
}
