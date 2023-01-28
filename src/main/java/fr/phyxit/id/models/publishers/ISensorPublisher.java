package fr.phyxit.id.models.publishers;

import fr.phyxit.id.models.Sensor;

public interface ISensorPublisher<T> {

    public abstract void publish();

    public abstract T appendGenericInformation(Sensor<?> associatedSensor, T payload);

    public abstract void sendMessage(T payload);


    public void initializeConnection(Sensor<?> associatedSensor);
    public void closeConnection();

}
