package fr.phyxit.id.models.providers;

import fr.phyxit.id.models.Sensor;
import fr.phyxit.id.models.SensorProviderType;
import org.eclipse.paho.client.mqttv3.*;
import fr.phyxit.id.serialization.mqtt.MessageType;

import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class AMQTTClientSensorProvider<T> implements ISensorProvider<T>, MqttCallback {

    private String topic;
    protected IMqttClient client;

    private Sensor<T> sensor;

    public AMQTTClientSensorProvider(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    protected abstract void parseMessage(byte[] payload);
    protected abstract MqttConnectOptions initializeMqttClient();
    protected void initializeSubscriber() throws MqttException {
        client.subscribe(getTopic(), 1);
        client.setCallback(this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] payload = message.getPayload();
        ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
        int messageType = byteBuffer.getInt();
        if (messageType == MessageType.SENSOR_DATA) {
            long mostSignificantBits = byteBuffer.getLong();
            long leastSignificantBits = byteBuffer.getLong();
            UUID sensorUUID = new UUID(mostSignificantBits, leastSignificantBits);
            if (!sensorUUID.equals(sensor.getUUID())) return;

            // Skip the 20 first bytes that described the message type and sensor UUID
            byte[] validPayload = new byte[payload.length - 20];
            System.arraycopy(payload, 20, validPayload, 0, validPayload.length);
            parseMessage(validPayload);
        }
    }

    @Override
    public void initializeConnection(Sensor<T> associatedSensor) {
        this.sensor = associatedSensor;
        String publisherId = associatedSensor.getName() + "-provider";
        try {
            MqttConnectOptions options = initializeMqttClient();
            client = new MqttClient(getTopic(), publisherId);
            client.connect(options);
            initializeSubscriber();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendMessage(MqttMessage message) {
        if (!client.isConnected()) return;
        try {
            client.publish(getTopic(), message);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SensorProviderType getType() {
        return SensorProviderType.TCP;
    }

    @Override
    public SensorProviderProtocol getProtocol() {
        return null;
    }

}
