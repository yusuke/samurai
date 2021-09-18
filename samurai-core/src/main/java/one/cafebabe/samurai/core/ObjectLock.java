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
package one.cafebabe.samurai.core;

import java.io.Serializable;
import java.util.List;

public class ObjectLock implements Serializable {
    private final SunThreadDump threadDump;
    private final List<StackLine> objects;
    private static final long serialVersionUID = -1899274398012153019L;

    public ObjectLock(SunThreadDump threadDump, List<StackLine> objects) {
        this.threadDump = threadDump;
        this.objects = objects;
    }

    public SunThreadDump getThreadDump() {
        return this.threadDump;
    }

    public List<StackLine> getObject() {
        return this.objects;
    }
}
