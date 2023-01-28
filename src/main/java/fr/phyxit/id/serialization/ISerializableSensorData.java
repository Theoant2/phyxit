package fr.phyxit.id.serialization;

import java.io.IOException;

public interface ISerializableSensorData<T, E1, E2> {

    public void serialize(T data, E1 outputResource, SensorSerializationManager sensorSerializationManager) throws IOException;
    public T deserialize(E2 inputResource, SensorSerializationManager sensorSerializationManager) throws IOException;

    public int getSerializationID();

}
