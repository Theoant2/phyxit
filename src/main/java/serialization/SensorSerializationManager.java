package serialization;

import models.*;
import models.frequency.ASensorFrequency;
import serialization.mqtt.MQTTSensorSerializationProtocol;
import serialization.mqtt.MQTTSerializationContext;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SensorSerializationManager {

    private Map<Class<?>, ASensorSerializationProtocol<?, ?, ?>> protocols = new HashMap<>();

    public SensorSerializationManager() {
        // Register defaults protocols
        registerProtocol(new MQTTSensorSerializationProtocol());
    }

    public void registerProtocol(ASensorSerializationProtocol<?, ?, ?> protocol) {
        protocols.put(protocol.getClass(), protocol);
    }

    public <E1, E2, V extends ASerializationContext<E1, E2>> Optional<ASensorSerializationProtocol<E1, E2, V>> getProtocol(Class<? extends ASensorSerializationProtocol<E1, E2, V>> protocolClass) {
        ASensorSerializationProtocol<?, ?, ?> protocol = protocols.get(protocolClass);
        if (protocol == null) return Optional.empty();
        return Optional.of((ASensorSerializationProtocol<E1, E2, V>) protocol);
    }

    public <E1, E2, V extends ASerializationContext<E1, E2>> void serializeClientSensor(ClientSensor<?> sensor, Class<? extends ASensorSerializationProtocol<E1, E2, V>> protocolClass,
                                                                                        E1 outputResource, Supplier<V> contextCreator) throws IOException {
        Optional<ASensorSerializationProtocol<E1, E2, V>> protocolOptional = getProtocol(protocolClass);
        if (protocolOptional.isEmpty())
            throw new IllegalArgumentException(String.format("No registered protocol found for class protocol class '%s'", protocolClass.getName()));

        ASensorSerializationProtocol<E1, E2, V> protocol = protocolOptional.get();

        V context = contextCreator.get();
        context.setSensorSerializationManager(this);
        context.setProtocol(protocol);

        protocol.serialize(sensor, outputResource, context);
    }

    public <E1, E2, V extends ASerializationContext<E1, E2>> ServerSensor<?> deserializeClientSensor(Class<? extends ASensorSerializationProtocol<E1, E2, V>> protocolClass,
                                                                                        E2 inputResource, Supplier<V> contextCreator) throws IOException {
        Optional<ASensorSerializationProtocol<E1, E2, V>> protocolOptional = getProtocol(protocolClass);
        if (protocolOptional.isEmpty())
            throw new IllegalArgumentException(String.format("No registered protocol found for class protocol class '%s'", protocolClass.getName()));

        ASensorSerializationProtocol<E1, E2, V> protocol = protocolOptional.get();

        V context = contextCreator.get();
        context.setSensorSerializationManager(this);
        context.setProtocol(protocol);

        return protocol.deserialize(inputResource, context);
    }

    public byte[] serializeSensorWithMQTTProtocol(ClientSensor<?> sensor) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        serializeClientSensor(sensor, MQTTSensorSerializationProtocol.class, dataOutputStream,
                () -> new MQTTSerializationContext("" /* Not used when serializing */));

        byte[] bytes = byteArrayOutputStream.toByteArray();

        dataOutputStream.close();
        byteArrayOutputStream.close();

        return bytes;
    }

    public ServerSensor<?> deserializeSensorWithMQTTProtocol(byte[] payload, String mqttTopic) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(payload);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        ServerSensor<?> sensor = deserializeClientSensor(MQTTSensorSerializationProtocol.class, dataInputStream,
                () -> new MQTTSerializationContext(mqttTopic));

        dataInputStream.close();
        byteArrayInputStream.close();

        return sensor;
    }

}
