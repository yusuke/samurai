package samurai.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class ThreadStatistic implements ThreadDumpRenderer, Serializable {
    private List<FullThreadDump> fullThreadDumps = new ArrayList<FullThreadDump>();

    private List<ThreadDumpSequence> threadDumpsList = new ArrayList<ThreadDumpSequence>();
    private static final long serialVersionUID = 871320558326468787L;

    public ThreadStatistic() {
    }

    public String getFirstThreadId() {
        return this.threadDumpsList.get(0).getId();
    }

    public synchronized void reset() {
        this.fullThreadDumps.clear();
        this.threadDumpsList.clear();
    }

    public void onFullThreadDump(FullThreadDump fullThreadDump) {
        this.fullThreadDumps.add(fullThreadDump);
        List<ThreadDumpSequence> newThreadDumpsList = new ArrayList<ThreadDumpSequence>(threadDumpsList.size());
        for (int i = 0; i < fullThreadDump.getThreadCount(); i++) {
            for (ThreadDumpSequence sequence :  threadDumpsList) {
                if (fullThreadDump.getThreadDump(i).getId().equals(sequence.getId())) {
                    newThreadDumpsList.add(sequence);
                    break;
                }
            }
        }
        threadDumpsList = newThreadDumpsList;
    }

    public void onThreadDump(ThreadDump threadDump) {
        int size = threadDumpsList.size();
        boolean found = false;
        for (int i = 0; i < size; i++) {
            ThreadDumpSequence dumps = threadDumpsList.get(i);
            if (dumps.getId().equals(threadDump.getId())) {
                found = true;
                dumps.addThreadDump(threadDump);
                break;
            }
        }
        if (!found) {
            threadDumpsList.add(new ThreadDumpSequence(threadDump, getFullThreadDumpCount() + 1));
        }
    }

    public ThreadDumpSequence getPreviousThreadDumps(String threadId) {
        ThreadDumpSequence lastThreadDumps = null;
        for (ThreadDumpSequence td : threadDumpsList) {
            if (td.getId().equals(threadId)) {
                break;
            }
            lastThreadDumps = td;
        }
        return lastThreadDumps;
    }

    public ThreadDumpSequence getNextThreadDumps(String threadId) {
        boolean found = false;
        ThreadDumpSequence nextThreadDumps = null;
        for (ThreadDumpSequence td : threadDumpsList) {
            if (found) {
                nextThreadDumps = td;
                break;
            }
            if (td.getId().equals(threadId)) {
                found = true;
            }
        }
        return nextThreadDumps;
    }

    public ThreadDumpSequence[] getStackTracesAsArray() {
        return threadDumpsList.toArray(new ThreadDumpSequence[threadDumpsList.size()]);
    }

    public int getFullThreadDumpCount() {
        return this.fullThreadDumps.size();
    }

    public FullThreadDump getFullThreadDump(int index) {
        return this.fullThreadDumps.get(index);
    }

    public List<FullThreadDump> getFullThreadDumps() {
        return fullThreadDumps;
    }

    public ThreadDumpSequence getStackTracesById(String id) {
        for (ThreadDumpSequence sequence :  threadDumpsList) {
            if (sequence.getId().equals(id)) {
                return sequence;
            }
        }
        throw new AssertionError("no thread dump with id:" + id + " found");
    }


}
