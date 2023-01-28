package fr.phyxit.id;

import fr.phyxit.id.models.*;
import fr.phyxit.id.models.frequency.ASensorFrequency;
import fr.phyxit.id.models.providers.SensorProviderProtocol;
import fr.phyxit.id.restapi.http.HTTPRestServer;
import fr.phyxit.id.serialization.SensorSerializationManager;
import fr.phyxit.id.serialization.mqtt.MessageType;
import fr.phyxit.id.sets.SensorSet;
import fr.phyxit.id.storage.StorageModes;
import fr.phyxit.id.storage.StorageSpace;
import org.eclipse.paho.client.mqttv3.*;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SensorManager {

    private StorageSpace<ServerSensor<?>> storageSpace;

    private SensorSerializationManager sensorSerializationManager;

    public SensorManager() {
        storageSpace = new StorageSpace<>(StorageModes.NAME_KEYMODE, StorageModes.UUID_KEYMODE, StorageModes.DATA_TYPE_KEYMODE,
                StorageModes.DATA_TYPE_CLASS_KEYMODE, StorageModes.LOC_KEYMODE, StorageModes.FREQUENCY_KEYMODE,
                StorageModes.PROVIDER_TYPE_KEYMODE, StorageModes.PROVIDER_PROTOCOL_KEYMODE);
        sensorSerializationManager = new SensorSerializationManager();
    }

    public void createSensor(SensorBuilder.Server<?> sensorBuilder) {
        ServerSensor<?> sensor = sensorBuilder.build();
        registerSensor(sensor);
    }

    private void registerSensor(ServerSensor<?> sensor) {
        String sensorName = sensor.getName();
        if (storageSpace.exists(sensorName))
            throw new IllegalArgumentException("There is already a sensor with the same name");
        sensor.initialize();
        storageSpace.register(sensor);
    }

    public void unregisterSensor(ServerSensor<?> sensor) {
        sensor.getFrequency().stopTimer();
        sensor.getProvider().closeConnection();
        storageSpace.remove(sensor);
    }
    public void unregisterSensor(String sensorName) {
        storageSpace.remove(getByName(sensorName));
    }

    public SensorSerializationManager getSensorSerializationManager() { return sensorSerializationManager; }

    public ServerSensor<?> getByUUID(UUID sensorUUID) { return storageSpace.getUnique(sensorUUID); }
    public ServerSensor<?> getByName(String sensorName) { return storageSpace.getUnique(sensorName); }

    public <T> SensorSet<T> getByDataType(ISensorDataType<T> sensorDataType) {
        Set<ServerSensor<?>> sensors = storageSpace.get(sensorDataType);
        return new SensorSet<>(sensors.stream().map(sensor -> (ServerSensor<T>) sensor)
                .collect(Collectors.toSet()));
    }

    public <T> SensorSet<T> getByDataType(Class<T> sensorDataType) {
        Set<ServerSensor<?>> sensors = storageSpace.get(sensorDataType);
        return new SensorSet<>(sensors.stream().map(sensor -> (ServerSensor<T>) sensor)
                .collect(Collectors.toSet()));
    }

    public Set<ServerSensor<?>> getByLoc(SensorLocation sensorLoc) { return storageSpace.get(sensorLoc); }

    public Set<ServerSensor<?>> getByFrequency(ASensorFrequency frequency) { return storageSpace.get(frequency); }

    public Set<ServerSensor<?>> getByProviderType(SensorProviderType sensorProviderType) { return storageSpace.get(sensorProviderType); }

    public Set<ServerSensor<?>> getByProviderProtocol(SensorProviderProtocol sensorProviderProtocol) { return storageSpace.get(sensorProviderProtocol); }

    public Set<ServerSensor<?>> getByMultipleAttributes(Object ... attributes) { return storageSpace.getInter(attributes); }

    public HTTPRestServer createRestAPIServer() {
        return new HTTPRestServer(this);
    }

    public void enableMQTTSensorDiscovery(String masterTopic) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);

        String publisherId = "SensorManager-AutoDiscovery";
        try {
            IMqttClient client = new MqttClient(masterTopic, publisherId);
            client.connect(options);

            client.subscribe(masterTopic);
            client.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    byte[] payload = message.getPayload();
                    int messageType = ByteBuffer.wrap(payload).getInt();
                    byte[] messageTypeCroppedPayload = new byte[payload.length - 4];
                    if (messageType == MessageType.SENSOR_DESCRIPTION) {
                        // Skip the 4 first bytes that described the message type
                        System.arraycopy(payload, 4, messageTypeCroppedPayload, 0, messageTypeCroppedPayload.length);
                        ServerSensor<?> serverSensor = sensorSerializationManager.deserializeSensorWithMQTTProtocol(messageTypeCroppedPayload, masterTopic);
                        System.out.println(String.format("Discovered new sensor: %s", serverSensor.getName()));
                        registerSensor(serverSensor);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {}
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

}
