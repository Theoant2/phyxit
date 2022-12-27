package models.publishers;

import models.Sensor;

public interface ISensorPublisher<T> {

    public abstract void publish();

    public abstract T appendGenericInformation(Sensor<?> associatedSensor, T payload);

    public abstract void sendMessage(T payload);


    public void initializeConnection(Sensor<?> associatedSensor);
    public void closeConnection();

}
