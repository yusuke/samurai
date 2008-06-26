/**
 * Samurai
 * Copyright 2003-2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

public class DeadLockingThreads {
    public static void main(String args[]) throws Exception {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Thread athread1 = new DeadLockingThread(obj1, obj2);
        athread1.setName("DeadLockThread1");
        athread1.start();

        Thread athread2 = new DeadLockingThread(obj2, obj1);
        athread2.setName("DeadLockThread2");
        athread2.start();

        Thread athread3 = new DeadLockingThread(obj2, obj1);
        athread3.setName("DeadLockThread3");
        athread3.start();

        while (true) {
            Thread.sleep(100000);
        }
    }
}

class DeadLockingThread extends Thread {
    Object lock1;
    Object lock2;

    public DeadLockingThread(Object arg1, Object arg2) {
        lock1 = arg1;
        lock2 = arg2;
    }

    public void run() {
        while (true) {
            try {
                synchronized (lock1) {
                    Thread.sleep(10);
                    synchronized (lock2) {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException ignore) {
            }
        }

    }
}