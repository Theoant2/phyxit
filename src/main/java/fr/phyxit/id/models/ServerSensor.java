package fr.phyxit.id.models;

import fr.phyxit.id.models.providers.ISensorProvider;
import fr.phyxit.id.models.frequency.ASensorFrequency;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ServerSensor<T> extends Sensor<T> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ISensorProvider<T> provider;

    public ServerSensor(String name, UUID uuid, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency, ISensorProvider<T> provider) {
        super(name, uuid, sensorDataType, sensorLoc, frequency);
        this.provider = provider;
    }

    public ServerSensor(String name, ISensorDataType<T> sensorDataType, SensorLocation sensorLoc, ASensorFrequency frequency, ISensorProvider<T> provider) {
        super(name, sensorDataType, sensorLoc, frequency);
        this.provider = provider;
    }


    public static SensorBuilder.Server<?> builder() { return new SensorBuilder.Server<>(); }

    @Override
    public void initialize() {
        if (initialized) return;
        provider.initializeConnection(this);
        frequency.attach(() -> {
            T data = provider.getLastReceivedData();
            System.out.printf("[%s] <%s> | ", LocalTime.now().format(DATE_TIME_FORMATTER), this.getName());
            if (data == null) {
                System.out.println("<nothing>");
            } else System.out.println("Last received data: " + data);
        });
        frequency.startTimer();
        initialized = true;
    }

    @Override
    public void stopServices() {
        if (!initialized) return;
        frequency.stopTimer();
        frequency.detachEverything();
        provider.closeConnection();
        initialized = false;
    }

    public ISensorProvider<T> getProvider() { return provider; }

    @Override
    public String toString() {
        return "ServerSensor{" +
                "provider=" + provider +
                ", initialized=" + initialized +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", sensorDataType=" + sensorDataType +
                ", sensorLoc=" + sensorLoc +
                ", frequency=" + frequency +
                '}';
    }

}
