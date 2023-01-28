package fr.phyxit.id.serialization.mqtt;

import fr.phyxit.id.serialization.ASerializationContext;
import fr.phyxit.id.serialization.ISerializableSensorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

public class MQTTSerializationContext extends ASerializationContext<DataOutputStream, DataInputStream> {

    private String mqttTopic;

    public MQTTSerializationContext(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    @Override
    public <T> T deserialize(DataInputStream inputResource) throws IOException {
        int serializationID = inputResource.readInt();

        Optional<ISerializableSensorData<T, DataOutputStream, DataInputStream>> serializerOptional = protocol.getSerializer(serializationID);
        if (serializerOptional.isEmpty())
            throw new IllegalArgumentException(String.format("Unable to find a serializer for serialization ID '%d'", serializationID));

        ISerializableSensorData<T, DataOutputStream, DataInputStream> serializer = serializerOptional.get();
        return serializer.deserialize(inputResource, sensorSerializationManager);
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

}
