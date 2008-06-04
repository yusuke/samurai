package samurai.util;

import java.util.ArrayList;
import java.util.List;

/**
 * a thread executes asynchronized task
 * <p>Title: Samurai</p>
 * <p>Description: a tabbed tail tool</p>
 * <p>Copyright: Copyright (c) Yusuke Yamamoto 2003-2006</p>
 * <p> </p>
 *
 * @author Yusuke Yamamoto
 * @version 2.0.5
 */
public class ExecuteThread extends Thread {
    private List<Schedule> tasks = new ArrayList<Schedule>();

    public ExecuteThread() {
        super("Execute Thread");
    }

    /**
     * method for runnable implementation
     */
    public void run() {
        Schedule schedule = null;
        while (true) {
            try {
                if (0 == tasks.size()) {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException ignore) {
            }
            synchronized (tasks) {
                if (0 < tasks.size()) {
                    schedule = tasks.get(0);
                }
            }
            if (null != schedule && schedule.getTimeToExecute() < System.currentTimeMillis()) {
                synchronized (tasks) {
                    tasks.remove(schedule);
                }
                schedule.getTask().execute();
                schedule = null;
            } else if (null != schedule) {
                long waitMillis = System.currentTimeMillis() - schedule.getTimeToExecute();
                if (0 < waitMillis) {
                    try {
                        synchronized (this) {
                            wait(waitMillis);
                        }
                        schedule = null;
                    } catch (InterruptedException ignore) {
                    }
                }
            }
//      try {
//        synchronized (this) {
////          if(null != laterTask){
////            wait(100);
////          }else{
////            wait(100000);
////          }
//          wait();
//        }
//      } catch (InterruptedException ioe) {
//        ioe.printStackTrace(System.out);
//      }
//      if (0 < tasks.size()) {
//        while (tasks.size() != 0) {
//          Schedule task = null;
//          synchronized (tasks) {
//            task = tasks.get(0);
//            if (task.getTimeToExecute() > System.currentTimeMillis()) {
//              tasks.remove(0);
//            } else {
//              task = null;
//            }
//          }
//          if (null != task) {
//            try {
//              task.getTask().execute();
//            } catch (Throwable th) {
//              th.printStackTrace();
//            }
//          }
//        }
//      }
////      if(null != laterTask && System.currentTimeMillis() > invokeTime){
////        Task task;
////        synchronized(this){
////          task = laterTask;
////          laterTask = null;
////        }
////          try {
////            task.execute();
////          } catch (Throwable th) {
////            th.printStackTrace();
////          }
////      }
        }
    }

    /**
     * add a task to be executed.
     * @param task task
     */

    public synchronized void addTask(Task task) {
        invokeLater(task, 0);
//    new Throwable().printStackTrace();
//    tasks.add(new Schedule(task,System.currentTimeMillis()));
//    this.notify();
    }
//  private Task laterTask = null;

    //  private long invokeTime;
    public synchronized void invokeLater(Task task, int seconds) {
        tasks.add(new Schedule(task, System.currentTimeMillis() + seconds * 1000));
        this.notify();
    }
}

class Schedule {
    private Task task;
    private long timeToExecute;

    Schedule(Task task, long timeToExecute) {
        this.task = task;
        this.timeToExecute = timeToExecute;
    }

    public Task getTask() {
        return task;
    }

    public long getTimeToExecute() {
        return timeToExecute;
    }
}
