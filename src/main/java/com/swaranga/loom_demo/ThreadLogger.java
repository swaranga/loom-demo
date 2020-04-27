
package com.swaranga.loom_demo;

import java.util.concurrent.*;
import java.lang.management.*;
import javax.management.*;

public class ThreadLogger {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void start() {
        executor.scheduleAtFixedRate(this::logThreadCount, 0, 1, TimeUnit.SECONDS);
    }

    private void logThreadCount() {
        int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
        int peakThreads = ManagementFactory.getThreadMXBean().getPeakThreadCount();

        System.out.println("Threads: current=" + threadCount + ", peak=" + peakThreads);
    }
}