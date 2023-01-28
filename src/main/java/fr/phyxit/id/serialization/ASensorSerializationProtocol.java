package fr.phyxit.id.serialization;

import fr.phyxit.id.models.ClientSensor;
import fr.phyxit.id.models.ServerSensor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ASensorSerializationProtocol<E1, E2, V extends ASerializationContext<E1, E2>> {

    private Map<TokenType<?, ?>, ISerializableSensorData<?, ?, ?>> serializersByClass = new HashMap<>();
    private Map<Integer, ISerializableSensorData<?, ?, ?>> serializersById = new HashMap<>();


    public <T> void registerSerializer(Class<T> clazz, ISerializableSensorData<T, ?, ?> serializer) {
        registerSerializer(TokenType.wrap(clazz), serializer);
    }

    public <T> void registerSerializer(TokenType<T, ?> tokenType, ISerializableSensorData<?, ?, ?> serializer) {
        int serializationId = serializer.getSerializationID();
        if (serializersById.containsKey(serializationId))
            throw new IllegalArgumentException(String.format("There is already a serializer with the id '%d': '%s'", serializationId, serializersById.get(serializationId)));
        if (serializersByClass.containsKey(tokenType))
            throw new IllegalArgumentException(String.format("There is already a serializer for the clazz '%s': '%s'", tokenType.getMainClass().getName(), serializersByClass.get(tokenType)));
        serializersById.put(serializationId, serializer);
        serializersByClass.put(tokenType, serializer);
    }

    public <T> Optional<ISerializableSensorData<T, E1, E2>> getSerializer(Class<T> clazz) { return getSerializer(TokenType.wrap(clazz)); }

    public <T> Optional<ISerializableSensorData<T, E1, E2>> getSerializer(TokenType<T, ?> type) {
        ISerializableSensorData<?, ?, ?> serializer = serializersByClass.get(type);
        if (serializer == null) return Optional.empty();
        return Optional.of((ISerializableSensorData<T, E1, E2>) serializer);
    }

    public <T> Optional<ISerializableSensorData<T, E1, E2>> getSerializer(int serializationId) {
        ISerializableSensorData<?, ?, ?> serializer = serializersById.get(serializationId);
        if (serializer == null) return Optional.empty();
        return Optional.of((ISerializableSensorData<T, E1, E2>) serializer);
    }

    public abstract void serialize(ClientSensor<?> sensor, E1 outputResource, V serializationContext) throws IOException;

    public abstract ServerSensor<?> deserialize(E2 inputResource, V serializationContext) throws IOException;

}
