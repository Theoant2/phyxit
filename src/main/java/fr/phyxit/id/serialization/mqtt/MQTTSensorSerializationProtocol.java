package fr.phyxit.id.serialization.mqtt;

import fr.phyxit.id.models.ClientSensor;
import fr.phyxit.id.models.ISensorDataType;
import fr.phyxit.id.models.SensorLocation;
import fr.phyxit.id.models.ServerSensor;
import fr.phyxit.id.models.frequency.ASensorFrequency;
import fr.phyxit.id.models.frequency.SimpleSensorFrequency;
import fr.phyxit.id.serialization.ASensorSerializationProtocol;
import fr.phyxit.id.serialization.TokenType;
import fr.phyxit.id.serialization.mqtt.data.ASensorDataTypeSerializer;
import fr.phyxit.id.serialization.mqtt.data.SimpleSensorFrequencySerializer;
import fr.phyxit.id.serialization.mqtt.data.SimpleSensorLocationSerializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MQTTSensorSerializationProtocol extends ASensorSerializationProtocol<DataOutputStream, DataInputStream, MQTTSerializationContext> {

    public MQTTSensorSerializationProtocol() {
        // Register defaults serializers
        registerSerializer(SimpleSensorFrequency.class, new SimpleSensorFrequencySerializer());
        registerSerializer(SensorLocation.Simple.class, new SimpleSensorLocationSerializer());
        registerSerializer(TokenType.from(ISensorDataType.class, Boolean.class), new ASensorDataTypeSerializer.Boolean());
        registerSerializer(TokenType.from(ISensorDataType.class, Double.class), new ASensorDataTypeSerializer.Double());
        registerSerializer(TokenType.from(ISensorDataType.class, Float.class), new ASensorDataTypeSerializer.Float());
        registerSerializer(TokenType.from(ISensorDataType.class, Integer.class), new ASensorDataTypeSerializer.Integer());
        registerSerializer(TokenType.from(ISensorDataType.class, Long.class), new ASensorDataTypeSerializer.Long());
        registerSerializer(TokenType.from(ISensorDataType.class, String.class), new ASensorDataTypeSerializer.String());
    }

    @Override
    public void serialize(ClientSensor<?> sensor, DataOutputStream outputResource, MQTTSerializationContext serializationContext) throws IOException {
        String sensorName = sensor.getName();
        outputResource.writeUTF(sensorName);

        UUID sensorUUID = sensor.getUUID();
        outputResource.writeLong(sensorUUID.getMostSignificantBits());
        outputResource.writeLong(sensorUUID.getLeastSignificantBits());

        SensorLocation sensorLocation = sensor.getSensorLoc();
        serializationContext.serialize(sensorLocation, TokenType.wrap(sensorLocation), outputResource);

        ISensorDataType sensorDataType = sensor.getSensorDataType();
        serializationContext.serialize(sensorDataType, TokenType.from(ISensorDataType.class, sensorDataType.getDataClass()), outputResource);

        ASensorFrequency sensorFrequency = sensor.getFrequency();
        serializationContext.serialize(sensorFrequency, TokenType.wrap(sensorFrequency), outputResource);
    }

    @Override
    public ServerSensor<?> deserialize(DataInputStream inputResource, MQTTSerializationContext serializationContext) throws IOException {
        String sensorName = inputResource.readUTF();

        long mostSignificantBits = inputResource.readLong();
        long leastSignificantBits = inputResource.readLong();
        UUID sensorUUID = new UUID(mostSignificantBits, leastSignificantBits);

        SensorLocation sensorLocation = serializationContext.deserialize(inputResource);
        ISensorDataType sensorDataType = serializationContext.deserialize(inputResource);
        ASensorFrequency sensorFrequency = serializationContext.deserialize(inputResource);

        return new ServerSensor<>(sensorName, sensorUUID, sensorDataType, sensorLocation, sensorFrequency, sensorDataType.createSensorProvider(serializationContext.getMqttTopic()));
    }

}
