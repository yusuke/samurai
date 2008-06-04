package samurai.tail;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Samurai</p>
 * <p/>
 * <p>Description: a thread dump analyzing tool</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2003,2004,2005,2006</p>
 * <p/>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class Tailer extends Thread {
    List<SingleLogWatcher> activeWatchers = new ArrayList<SingleLogWatcher>(0);
    List<SingleLogWatcher> inactiveWatchers = new ArrayList<SingleLogWatcher>(0);
    private long lastCheck;
    private static Tailer tailer = new Tailer();

    /*package*/
    static Tailer getTailer() {
        return tailer;
    }

    private Tailer() {
        super();
        super.setName("LogWatcher Thread");
        this.setDaemon(true);
        this.setPriority(Thread.MIN_PRIORITY);
        this.start();
    }

    public synchronized void addLogWatcher(SingleLogWatcher watcher) {
        activeWatchers.add(watcher);
    }

    public void run() {
        lastCheck = System.currentTimeMillis();
        while (true) {
            for (int i = 0; i < activeWatchers.size(); i++) {
                SingleLogWatcher watcher = activeWatchers.get(i);
                watcher.checkUpdate();
                synchronized (activeWatchers) {
                    if (watcher.isDead()) {
                        activeWatchers.remove(i);
                        i--;
                    } else if (watcher.isCheckingUpdate()) {
                        inactiveWatchers.add(activeWatchers.remove(i));
                        i--;
                    }
                }
            }
            if (50 < (System.currentTimeMillis() - lastCheck)) {
//        System.out.println("check inactive watcher"+inactiveWatchers.size()+" "+activeWatchers.size());
                for (int i = 0; i < inactiveWatchers.size(); i++) {
                    SingleLogWatcher watcher = inactiveWatchers.get(i);
                    watcher.checkUpdate();
                    if (watcher.isDead()) {
                        inactiveWatchers.remove(i);
                        i--;
                    } else if (!watcher.isCheckingUpdate()) {
                        activeWatchers.add(inactiveWatchers.remove(i));
                        i--;
                    }
                }
                lastCheck = System.currentTimeMillis();
            }
            if (activeWatchers.size() == 0) {
//        System.out.println("sleep");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ie) {
                }
            }
        }
    }
}
