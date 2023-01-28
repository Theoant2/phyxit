package fr.phyxit.id.serialization.mqtt.data;

import fr.phyxit.id.models.frequency.SimpleSensorFrequency;
import fr.phyxit.id.serialization.SensorSerializationManager;
import fr.phyxit.id.serialization.mqtt.AMQTTSerializableSensorData;
import fr.phyxit.id.serialization.mqtt.SerializationID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SimpleSensorFrequencySerializer extends AMQTTSerializableSensorData<SimpleSensorFrequency> {

    @Override
    public void serializeObject(SimpleSensorFrequency data, DataOutputStream outputStream, SensorSerializationManager sensorSerializationManager) throws IOException {
        long delay = data.getDelay();
        long period = data.getPeriod();
        outputStream.writeLong(delay);
        outputStream.writeLong(period);
    }

    @Override
    public SimpleSensorFrequency deserializeObject(DataInputStream inputStream, SensorSerializationManager sensorSerializationManager) throws IOException {
        long delay = inputStream.readLong();
        long period = inputStream.readLong();
        return new SimpleSensorFrequency(delay, period);
    }

    @Override
    public int getSerializationID() {
        return SerializationID.SIMPLE_SENSOR_FREQUENCY;
    }

}
