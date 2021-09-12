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
package samurai.util;

import java.util.ArrayList;
import java.util.List;

public class ExecuteThread extends Thread {
    private final List<Schedule> tasks = new ArrayList<>();

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
     *
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
    private final Task task;
    private final long timeToExecute;

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
