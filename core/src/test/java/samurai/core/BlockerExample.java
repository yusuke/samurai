/**
 * Samurai
 * Copyright 2008, Yusuke Yamamoto.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package samurai.core;

public class BlockerExample {
    public static void main(String[] args) {
        Thread thread1 = new AThread(args);
        thread1.start();
        Thread thread2 = new AThread(args);
        thread2.start();
    }
}

class AThread extends Thread {
    Object OBJECT;

    AThread(Object obj) {
        this.OBJECT = obj;
    }

    public void run() {
        while (true) {
            synchronized (OBJECT) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}
