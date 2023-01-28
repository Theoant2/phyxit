package fr.phyxit.id.serialization.mqtt.data;

import fr.phyxit.id.models.ISensorDataType;
import fr.phyxit.id.serialization.SensorSerializationManager;
import fr.phyxit.id.serialization.mqtt.AMQTTSerializableSensorData;
import fr.phyxit.id.serialization.mqtt.SerializationID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ASensorDataTypeSerializer<T extends ISensorDataType<?>> extends AMQTTSerializableSensorData<T> {

    @Override
    public void serializeObject(T data, DataOutputStream outputStream, SensorSerializationManager sensorSerializationManager) throws IOException {}

    @Override
    public T deserializeObject(DataInputStream inputStream, SensorSerializationManager sensorSerializationManager) throws IOException {
        return getSensorDataType();
    }

    public abstract T getSensorDataType();

    public static class Boolean extends ASensorDataTypeSerializer<ISensorDataType<java.lang.Boolean>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_BOOLEAN; }
        @Override
        public ISensorDataType<java.lang.Boolean> getSensorDataType() { return ISensorDataType.MQTT_BOOLEAN; }
    }

    public static class Double extends ASensorDataTypeSerializer<ISensorDataType<java.lang.Double>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_DOUBLE; }
        @Override
        public ISensorDataType<java.lang.Double> getSensorDataType() { return ISensorDataType.MQTT_DOUBLE; }
    }

    public static class Float extends ASensorDataTypeSerializer<ISensorDataType<java.lang.Float>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_FLOAT; }
        @Override
        public ISensorDataType<java.lang.Float> getSensorDataType() { return ISensorDataType.MQTT_FLOAT; }
    }

    public static class Integer extends ASensorDataTypeSerializer<ISensorDataType<java.lang.Integer>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_INTEGER; }
        @Override
        public ISensorDataType<java.lang.Integer> getSensorDataType() { return ISensorDataType.MQTT_INTEGER; }
    }

    public static class Long extends ASensorDataTypeSerializer<ISensorDataType<java.lang.Long>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_LONG; }
        @Override
        public ISensorDataType<java.lang.Long> getSensorDataType() { return ISensorDataType.MQTT_LONG; }
    }

    public static class String extends ASensorDataTypeSerializer<ISensorDataType<java.lang.String>> {
        @Override
        public int getSerializationID() { return SerializationID.SENSOR_DATA_TYPE_STRING; }
        @Override
        public ISensorDataType<java.lang.String> getSensorDataType() { return ISensorDataType.MQTT_STRING; }
    }

}
