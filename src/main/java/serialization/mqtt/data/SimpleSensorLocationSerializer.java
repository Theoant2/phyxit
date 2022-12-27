package serialization.mqtt.data;

import models.SensorLocation;
import serialization.ISerializableSensorData;
import serialization.SensorSerializationManager;
import serialization.mqtt.AMQTTSerializableSensorData;
import serialization.mqtt.SerializationID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimpleSensorLocationSerializer extends AMQTTSerializableSensorData<SensorLocation.Simple> {

    @Override
    public void serializeObject(SensorLocation.Simple data, DataOutputStream outputStream, SensorSerializationManager sensorSerializationManager) throws IOException {
        String locationName = data.getLocationName();
        outputStream.writeUTF(locationName);
    }

    @Override
    public SensorLocation.Simple deserializeObject(DataInputStream inputStream, SensorSerializationManager sensorSerializationManager) throws IOException {
        String locationName = inputStream.readUTF();
        return new SensorLocation.Simple(locationName);
    }

    @Override
    public int getSerializationID() {
        return SerializationID.SIMPLE_SENSOR_LOCATION;
    }

}
