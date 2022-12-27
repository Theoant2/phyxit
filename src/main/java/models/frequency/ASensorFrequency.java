package models.frequency;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class ASensorFrequency extends TimerTask {

    private Timer timer;
    private Set<Runnable> callbacks = new HashSet<>();

    public final void startTimer() {
        if (timer != null) stopTimer();
        timer = new Timer();
        innerStartTimer(timer);
    }
    public abstract void innerStartTimer(Timer timer);

    public final void stopTimer() {
        if (timer == null) return;
        timer.cancel();
        timer = null;
    }

    public final void restart() {
        stopTimer();
        startTimer();
    }

    public final void attach(Runnable callback) {
        callbacks.add(callback);
    }

    public final void detachEverything() { callbacks.clear(); }

    protected final void notifyCallback() { callbacks.forEach(Runnable::run); }


    @Override
    public String toString() {
        return "ASensorFrequency{" +
                "timer=" + timer +
                ", callbacks=" + callbacks +
                '}';
    }

    public static SimpleSensorFrequency simple(TimeUnit timeUnit, long delay, long period) {
        return new SimpleSensorFrequency(timeUnit.toMillis(delay), timeUnit.toMillis(period));
    }
    public static SimpleSensorFrequency simple(TimeUnit timeUnit, long period) { return simple(timeUnit, 0, period);
    }

}
