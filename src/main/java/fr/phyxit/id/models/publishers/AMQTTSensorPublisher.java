package fr.phyxit.id.models.publishers;

import fr.phyxit.id.models.Sensor;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.paho.client.mqttv3.*;
import fr.phyxit.id.serialization.mqtt.MessageType;

import java.nio.ByteBuffer;

public abstract class AMQTTSensorPublisher implements ISensorPublisher<Byte[]> {

    private String topic;
    private Sensor<?> sensor;
    protected IMqttClient client;

    public AMQTTSensorPublisher(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    protected abstract MqttConnectOptions initializeMqttClient();

    @Override
    public void initializeConnection(Sensor<?> associatedSensor) {
        this.sensor = associatedSensor;
        String publisherId = associatedSensor.getName() + "-publisher";
        try {
            MqttConnectOptions options = initializeMqttClient();
            client = new MqttClient(getTopic(), publisherId);
            client.connect(options);
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

    @Override
    public Byte[] appendGenericInformation(Sensor<?> associatedSensor, Byte[] payload) {
        byte[] messageHeader = ByteBuffer.allocate(20)
                .putInt(MessageType.SENSOR_DATA)
                .putLong(associatedSensor.getUUID().getMostSignificantBits())
                .putLong(associatedSensor.getUUID().getLeastSignificantBits())
                .array();
        return concatByteArrays(ArrayUtils.toObject(messageHeader), payload);
    }

    public void sendMessage(byte[] payload) {
        sendMessage(ArrayUtils.toObject(payload));
    }

    @Override
    public void sendMessage(Byte[] payload) {
        Byte[] completePayload = appendGenericInformation(sensor, payload);
        sendMessage(new MqttMessage(ArrayUtils.toPrimitive(completePayload)));
    }

    private void sendMessage(MqttMessage message) {
        if (!client.isConnected()) return;
        try {
            client.publish(getTopic(), message);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private Byte[] concatByteArrays(Byte[] array1, Byte[] array2) {
        Byte[] result = new Byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

}
