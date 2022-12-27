package serialization.mqtt.data;

import models.frequency.SimpleSensorFrequency;
import serialization.ISerializableSensorData;
import serialization.SensorSerializationManager;
import serialization.mqtt.AMQTTSerializableSensorData;
import serialization.mqtt.SerializationID;

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
