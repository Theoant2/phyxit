package fr.phyxit.id.serialization;

import java.io.IOException;
import java.util.Optional;

public abstract class ASerializationContext<E1, E2> {

    protected ASensorSerializationProtocol<E1, E2, ?> protocol;
    protected SensorSerializationManager sensorSerializationManager;

    public void setSensorSerializationManager(SensorSerializationManager sensorSerializationManager) {
        this.sensorSerializationManager = sensorSerializationManager;
    }

    void setProtocol(ASensorSerializationProtocol<E1, E2, ?> protocol) {
        this.protocol = protocol;
    }

    public <T> void serialize(T object, TokenType<T, ?> type, E1 outputResource) throws IOException {
        Optional<ISerializableSensorData<T, E1, E2>> serializerOptional = protocol.getSerializer(type);
        if (serializerOptional.isEmpty())
            throw new IllegalArgumentException(String.format("Unable to find a serializer for data type '%s'", type));

        ISerializableSensorData<T, E1, E2> serializer = serializerOptional.get();
        serializer.serialize(object, outputResource, sensorSerializationManager);
    }

    public abstract <T> T deserialize(E2 inputResource) throws IOException;

}
