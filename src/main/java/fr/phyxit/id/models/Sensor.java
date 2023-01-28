package fr.phyxit.id.models;

import fr.phyxit.id.models.frequency.ASensorFrequency;

import java.util.Objects;
import java.util.UUID;

public abstract class Sensor<T> {

    protected boolean initialized = false;

    protected UUID uuid;

    protected final String name;
    protected final ISensorDataType<T> sensorDataType;
    protected final SensorLocation sensorLoc;
    protected final ASensorFrequency frequency;

    public Sensor(String name, UUID uuid, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency) {
        this.name = name;
        this.uuid = uuid;
        this.sensorDataType = sensorDataType;
        this.sensorLoc = sensorLoc;
        this.frequency = frequency;
    }

    public Sensor(String name, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency) {
        this(name, UUID.randomUUID(), sensorDataType, sensorLoc, frequency);
    }

    public abstract void initialize();

    public abstract void stopServices();

    public UUID getUUID() { return uuid; }

    public String getName() {
        return name;
    }

    public ISensorDataType<T> getSensorDataType() {
        return sensorDataType;
    }

    public SensorLocation getSensorLoc() {
        return sensorLoc;
    }

    public ASensorFrequency getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "name='" + name + '\'' +
                ", sensorDataType=" + sensorDataType +
                ", sensorLoc=" + sensorLoc +
                ", frequency=" + frequency +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor<?> sensor = (Sensor<?>) o;
        return Objects.equals(name, sensor.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
