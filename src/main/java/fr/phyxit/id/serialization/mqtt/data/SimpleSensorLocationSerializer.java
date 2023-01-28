package fr.phyxit.id.serialization.mqtt.data;

import fr.phyxit.id.models.SensorLocation;
import fr.phyxit.id.serialization.SensorSerializationManager;
import fr.phyxit.id.serialization.mqtt.AMQTTSerializableSensorData;
import fr.phyxit.id.serialization.mqtt.SerializationID;

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
