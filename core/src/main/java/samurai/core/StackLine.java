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
    private final boolean IS_LINE;
    private final boolean IS_NATIVE_METHOD;
    private final String METHOD_NAME;
    private final String SOURCE;
    private final String LINE_NUMBER;
    private final String CLASS_NAME;

    private final boolean IS_TRYING_TO_GET_LOCK;
    private final boolean IS_HOLDING_LOCK;
    private final String LOCKED_OBJECT_ID;
    private final String LOCKED_CLASS_NAME;

    //  private List stateList;
    public StackLine(String line) {
        this.line = line;
        IS_LINE = line.trim().startsWith("at");
        if (IS_LINE) {
            int methodIndexBegin = line.lastIndexOf(".", line.indexOf("(")) + 1;
            int methodIndexEnd = line.indexOf("(");
            METHOD_NAME = line.substring(methodIndexBegin, methodIndexEnd);
        } else {
            METHOD_NAME = "n/a";
        }

        IS_NATIVE_METHOD = -1 != line.indexOf("(Native Method");

        if (IS_LINE) {
            if (IS_NATIVE_METHOD) {
                SOURCE = "Native Method";
            } else {
                int sourceIndexBegin = line.indexOf("(") + 1;
                int sourceIndexEnd = line.indexOf(":");
                if (-1 != sourceIndexEnd) {
                    SOURCE = line.substring(sourceIndexBegin, sourceIndexEnd);
                } else {
                    SOURCE = "Unknown Source";
                }
            }
        } else {
            SOURCE = "n/a";
        }

        if (IS_NATIVE_METHOD) {
            LINE_NUMBER = "Native Method";
        } else {
            int lineIndexBegin = line.lastIndexOf(":") + 1;
            int lineIndexEnd = line.lastIndexOf(")");
            if (-1 != lineIndexBegin && -1 != lineIndexEnd) {
//                System.out.println("line:"+line);
//                System.out.println("begin:"+lineIndexBegin);
//                System.out.println("end:"+lineIndexEnd);
                LINE_NUMBER = line.substring(lineIndexBegin, lineIndexEnd);
            } else {
                LINE_NUMBER = "Unknown Source";
            }
        }

        if (IS_LINE) {
            int classIndexBegin = line.indexOf("at ") + 3;
            int classIndexEnd = line.lastIndexOf(".", line.indexOf("("));
            CLASS_NAME = line.substring(classIndexBegin, classIndexEnd);
        } else {
            CLASS_NAME = "n/a";
        }

        IS_TRYING_TO_GET_LOCK = -1 != line.indexOf("- waiting to lock");
        IS_HOLDING_LOCK = -1 != line.indexOf("- locked");
        if (IS_HOLDING_LOCK || IS_TRYING_TO_GET_LOCK) {
            LOCKED_OBJECT_ID = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
            LOCKED_CLASS_NAME = line.substring(line.indexOf("(a ") + 3, line.indexOf(")"));
        }else{
            LOCKED_OBJECT_ID = "n/a";
            LOCKED_CLASS_NAME = "n/a";
        }
    }


    /**
     * tests the StackLine is represents a java source line
     *
     * @return boolean
     */

    public boolean isLine() {
        return IS_LINE;
    }

    public String getMethodName() {
        return METHOD_NAME;
    }

    public String getSource() {
        return SOURCE;
    }

    public String getClassName() {
        return CLASS_NAME;
    }


    public String getLine() {
        return this.line;
    }

    public String getLineNumber() {
        return LINE_NUMBER;
    }

    public boolean isNativeMethod() {
        return IS_NATIVE_METHOD;
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
        return IS_TRYING_TO_GET_LOCK;
    }

    public boolean isHoldingLock() {
        return IS_HOLDING_LOCK;
    }

    private String blockerId = null;

    /*package*/ void setBlockerId(String id) {
        this.blockerId = id;
    }

    public String getBlockerId() {
        return this.blockerId;
    }

    public String getLockedObjectId() {
        return LOCKED_OBJECT_ID;
    }

    public String getLockedClassName() {
        return LOCKED_CLASS_NAME;
    }
}
