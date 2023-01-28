package fr.phyxit.id.models.providers;

import fr.phyxit.id.models.Sensor;
import fr.phyxit.id.models.SensorProviderType;

public interface ISensorProvider<T> {

    public void initializeConnection(Sensor<T> associatedSensor);
    public void closeConnection();

    public T getLastReceivedData();

    public SensorProviderType getType();
    public SensorProviderProtocol getProtocol();

}
