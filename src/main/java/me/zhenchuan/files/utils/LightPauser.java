package me.zhenchuan.files.utils;

import me.zhenchuan.files.utils.Pauser;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
/**
 * Created by liuzhenchuan@foxmail.com on 1/15/15.
 */
public class LightPauser implements Pauser {
    private final AtomicBoolean pausing = new AtomicBoolean();
    private final int busyCount;
    private final long parkPeriod;
    private int count;
    private volatile Thread thread;

    public LightPauser(int busyCount, long parkPeriod) {
        this.busyCount = busyCount;
        this.parkPeriod = parkPeriod;
    }

    @Override
    public void reset() {
        count = 0;
    }

    @Override
    public void pause() {
        if (count++ < busyCount)
            return;
        thread = Thread.currentThread();
        pausing.set(true);
        LockSupport.parkNanos(parkPeriod);
        pausing.set(false);
        reset();
    }

    @Override
    public void unpause() {
        if (pausing.get())
            LockSupport.unpark(thread);
    }
}
