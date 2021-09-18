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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public final class StringUtil {
    private StringUtil() {
        //this class is not intended to be instantiated
    }

    public static int lastIndexOfIgnoreCase(String str, String searchStr, int startPos) {
        for (int i = startPos; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(Document str, String searchStr, int startPos, boolean ignoreCase) {
        int length = searchStr.length();
        String buf;
        if (ignoreCase) {
            for (int i = startPos; i >= 0; i--) {
                try {
                    buf = str.getText(i, length);
                    if (buf.equalsIgnoreCase(searchStr)) {
                        return i;
                    }
                } catch (BadLocationException ex) {
                }
            }
        } else {
            for (int i = startPos; i >= 0; i--) {
                try {
                    buf = str.getText(i, length);
                    if (buf.equals(searchStr)) {
                        return i;
                    }
                } catch (BadLocationException ex) {
                }
            }
        }
        return -1;
    }

    public static int indexOf(Document str, String searchStr, int startPos, boolean ignoreCase) {
        int length = searchStr.length();
        int end = str.getLength();
        String buf;
        if (ignoreCase) {
            for (int i = startPos; i < end; i++) {
                try {
                    buf = str.getText(i, length);
                    if (buf.equalsIgnoreCase(searchStr)) {
                        return i;
                    }
                } catch (BadLocationException ex) {
                }
            }
        } else {
            for (int i = startPos; i < end; i++) {
                try {
                    buf = str.getText(i, length);
                    if (buf.equals(searchStr)) {
                        return i;
                    }
                } catch (BadLocationException ex) {
                }
            }
        }
        return -1;
    }

    public static int indexOfIgnoreCase(String str, String searchStr, int startPos) {
        int lastIndex = str.length() - searchStr.length();
        for (int i = startPos; i < lastIndex; i++) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    private final static char[] escapeTarget = {'+', '*', '?', '.', '[', '^', '$', '(', ')', '{', '}', '|', '\\'};

    public static String regexpEscape(String original) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < original.length(); i++) {
            for (int j = 0; j < escapeTarget.length; j++) {
                if (original.charAt(i) == escapeTarget[j]) {
                    buffer.append('\\');

                }
            }
            buffer.append(original.charAt(i));
        }
        return buffer.toString();
    }
}
