package models.frequency;

import java.util.Objects;
import java.util.Timer;

public class SimpleSensorFrequency extends ASensorFrequency {

    private long delay;
    private long period;

    public SimpleSensorFrequency(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    @Override
    public void innerStartTimer(Timer timer) {
        timer.scheduleAtFixedRate(this, delay, period);
    }

    @Override
    public void run() {
        notifyCallback();
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleSensorFrequency that = (SimpleSensorFrequency) o;
        return delay == that.delay && period == that.period;
    }

    @Override
    public int hashCode() {
        return Objects.hash(delay, period);
    }

    @Override
    public String toString() {
        return "SimpleSensorFrequency{" +
                "delay=" + delay +
                ", period=" + period +
                '}';
    }

}
