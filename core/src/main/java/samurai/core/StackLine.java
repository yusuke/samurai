package samurai.core;

import java.io.Serializable;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public abstract class StackLine implements Serializable {
    protected String line;
    private static final long serialVersionUID = -4322785746927805891L;

    //  private List stateList;
    public StackLine(String line) {
        this.line = line;
    }

    /**
     * tests the StackLine is represents a java source line
     *
     * @return boolean
     */

    public boolean isLine() {
        return getLine().trim().startsWith("at");
    }

    public String getMethodName() {
        if (isLine()) {
            int methodIndexBegin = line.lastIndexOf(".", line.indexOf("(")) + 1;
            int methodIndexEnd = line.indexOf("(");
            return line.substring(methodIndexBegin, methodIndexEnd);
        } else {
            return "n/a";
        }
    }

    public String getSource() {
        if (isLine()) {
            if (isNativeMethod()) {
                return "Native Method";
            } else {
                int sourceIndexBegin = line.indexOf("(") + 1;
                int sourceIndexEnd = line.indexOf(":");
                if (-1 != sourceIndexEnd) {
                    return line.substring(sourceIndexBegin, sourceIndexEnd);
                } else {
                    return "Unknown Source";
                }
            }
        } else {
            return "n/a";
        }
    }

    public String getClassName() {
        if (isLine()) {
            int classIndexBegin = line.indexOf("at ") + 3;
            int classIndexEnd = line.lastIndexOf(".", line.indexOf("("));
            return line.substring(classIndexBegin, classIndexEnd);
        } else {
            return "n/a";
        }
    }


    public String getLine() {
        return this.line;
    }

    public String getLineNumber() {
        if (isNativeMethod()) {
            return "Native Method";
        } else {
            int lineIndexBegin = line.indexOf(":") + 1;
            int lineIndexEnd = line.indexOf(")");
            if (0 != lineIndexBegin) {
                return line.substring(lineIndexBegin, lineIndexEnd);
            } else {
                return "Unknown Source";
            }
        }
    }

    public boolean isNativeMethod() {
        return -1 != line.indexOf("(Native Method");
    }

    public String toString() {
        return line;
    }

    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof StackLine)) {
            return false;
        }
        StackLine that = (StackLine) obj;
        return that.getLine().equals(this.getLine());
    }

    public boolean isTryingToGetLock() {
        return -1 != line.indexOf("- waiting to lock");
    }

    public boolean isHoldingLock() {
        return -1 != line.indexOf("- locked");
    }

    private String blockerId = null;

    /*package*/ void setBlockerId(String id) {
        this.blockerId = id;
    }

    public String getBlockerId() {
        return this.blockerId;
    }

    //  public abstract boolean isTryingToGetLock();
    public String getLockedObjectId() {
        return line.substring(line.indexOf("<") + 1, line.indexOf(">"));
    }

    public String getLockedClassName() {
        return line.substring(line.indexOf("(a ") + 3, line.indexOf(")"));
    }

//    public String asHTML(int index, boolean shrink) {
//        if (isHoldingLock()) {
//            String objId = getLockedObjectId();
//            int objIdBegin = line.indexOf(objId);
//            StringBuffer html = new StringBuffer();
//            html.append(escape(line.substring(0, objIdBegin)));
//            if (-1 != index) {
//                html.append("<a name=\"").append(objId).append("_").append(index).append("\"></a>");
//            } else {
//                html.append("<a name=\"").append(objId).append("\"></a>");
//            }
//            html.append(objId);
//            html.append(escape(line.substring(objIdBegin + objId.length())));
//            return html.toString();
//        } else if (isTryingToGetLock() && null != getLockedObjectId()) {
//            String objId = getLockedObjectId();
//            int objIdBegin = line.indexOf(objId);
//            StringBuffer html = new StringBuffer();
//            html.append(escape(line.substring(0, objIdBegin)));
//            if (-1 != index) {
//                html.append("<a href=\"../sequence/threadId-").append(this.getBlockerId()).append("_shrink-").append(shrink).append(".html#").append(objId).append("_").append(index).append("\">");
//            } else {
//                html.append("<a href=\"#").append(objId).append("\">");
//            }
//            html.append(objId);
//            html.append("</a>");
//            html.append(escape(line.substring(objIdBegin + objId.length())));
//            return html.toString();
//        } else {
//            return escape(line);
//        }
//    }
//
//    public String asHTML() {
//        return asHTML(-1, false);
//    }
//
//    public String escape(String from) {
//        StringBuffer to = new StringBuffer(from.length());
//        for (int i = 0; i < from.length(); i++) {
//            char theChar = from.charAt(i);
//            if ('<' == theChar) {
//                to.append("&lt;");
//            } else if ('>' == theChar) {
//                to.append("&gt;");
//            } else {
//                to.append(theChar);
//            }
//        }
//        return to.toString();
//    }
}
