package models;

import models.frequency.ASensorFrequency;
import models.publishers.ISensorPublisher;

import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;

public class ClientSensor<T> extends Sensor<T> {

    private ISensorPublisher publisher;

    public ClientSensor(String name, UUID uuid, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency, ISensorPublisher publisher) {
        super(name, uuid, sensorDataType, sensorLoc, frequency);
        this.publisher = publisher;
    }

    public ClientSensor(String name, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency, ISensorPublisher publisher) {
        super(name, sensorDataType, sensorLoc, frequency);
        this.publisher = publisher;
    }

    public static SensorBuilder.Client<?> builder() { return new SensorBuilder.Client<>(); }

    @Override
    public void initialize() {
        if (initialized) return;
        publisher.initializeConnection(this);
        frequency.attach(publisher::publish);
        frequency.startTimer();
        initialized = true;
    }

    @Override
    public void stopServices() {
        if (!initialized) return;
        frequency.stopTimer();
        frequency.detachEverything();
        publisher.closeConnection();
        initialized = false;
    }

    public ISensorPublisher getPublisher() { return publisher; }

    @Override
    public String toString() {
        return "ClientSensor{" +
                "publisher=" + publisher +
                ", initialized=" + initialized +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", sensorDataType=" + sensorDataType +
                ", sensorLoc=" + sensorLoc +
                ", frequency=" + frequency +
                '}';
    }

}
