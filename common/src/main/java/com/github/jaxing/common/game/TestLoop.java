package com.github.jaxing.common.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author cjxin
 * @date 2023/10/13
 */
@Data
public class TestLoop {

    public static final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(2);

    public static final long TIME_SLICE = 10;

    private List<String> player;

    private volatile int index;

    private long startTime;

    public TestLoop() {
        this.player = new ArrayList<>();
        this.player.add("A");
        this.player.add("B");
        this.player.add("C");
        this.player.add("D");
        this.player.add("E");

        this.startTime = System.currentTimeMillis();
        this.index = 0;
    }

    public static void main(String[] args) {
        TestLoop testLoop = new TestLoop();
        ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
            testLoop.setIndex((testLoop.getIndex() + 1) % testLoop.getPlayer().size());
            System.out.println(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - testLoop.startTime));
        }, TIME_SLICE, TimeUnit.SECONDS);
        // new Thread(() -> {
        //     while (true){
        //         String x = testLoop.getPlayer().get(testLoop.getIndex());
        //         System.out.println(x);
        //         if (x.equals("E")){
        //             scheduledFuture.cancel(true);
        //             break;
        //         }
        //         try {
        //             Thread.sleep(500);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // }).start();
    }
}
