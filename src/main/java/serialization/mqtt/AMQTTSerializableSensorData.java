package serialization.mqtt;

import serialization.ISerializableSensorData;
import serialization.SensorSerializationManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class AMQTTSerializableSensorData<T> implements ISerializableSensorData<T, DataOutputStream, DataInputStream> {

    @Override
    public void serialize(T data, DataOutputStream outputResource, SensorSerializationManager sensorSerializationManager) throws IOException {
        outputResource.writeInt(getSerializationID());
        serializeObject(data, outputResource, sensorSerializationManager);
    }

    public abstract void serializeObject(T data, DataOutputStream outputResource, SensorSerializationManager sensorSerializationManager) throws IOException;

    @Override
    public T deserialize(DataInputStream inputResource, SensorSerializationManager sensorSerializationManager) throws IOException {
        return deserializeObject(inputResource, sensorSerializationManager);
    }

    public abstract T deserializeObject(DataInputStream inputResource, SensorSerializationManager sensorSerializationManager) throws IOException;

}
