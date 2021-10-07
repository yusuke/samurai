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
package one.cafebabe.samurai.core.example;

@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
public class DeadLockingThreads {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("💀☠️🏴‍☠️☠This application is going to dead lock.💀☠️🏴‍☠️☠");
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

@SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
class DeadLockingThread extends Thread {
    final Object lock1;
    final Object lock2;

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