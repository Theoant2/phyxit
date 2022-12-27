package models.providers;

import models.Sensor;
import models.SensorProviderType;

public interface ISensorProvider<T> {

    public void initializeConnection(Sensor<T> associatedSensor);
    public void closeConnection();

    public T getLastReceivedData();

    public SensorProviderType getType();
    public SensorProviderProtocol getProtocol();

}
