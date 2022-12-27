import models.ClientSensor;
import models.SensorBuilder;
import org.eclipse.paho.client.mqttv3.*;
import serialization.mqtt.MessageType;
import serialization.SensorSerializationManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SensorPublisherManager {

    private Set<ClientSensor<?>> sensors = new HashSet<>();

    private SensorSerializationManager sensorSerializationManager = new SensorSerializationManager();

    private MqttConnectOptions options;
    private IMqttClient client;
    private String advsertiseTopic;

    public SensorPublisherManager(String advsertiseTopic) {
        this.advsertiseTopic = advsertiseTopic;

        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);

        String publisherId = "SensorPublisherManager-AutoDiscovery";
        try {
            client = new MqttClient(advsertiseTopic, publisherId);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void spin() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerSensorAndAdvertise(SensorBuilder.Client<?> sensorBuilder) throws IOException, MqttException {
        client.connect(options);

        ClientSensor<?> sensor = registerSensor(sensorBuilder);
        byte[] payload = sensorSerializationManager.serializeSensorWithMQTTProtocol(sensor);
        byte[] messageHeader = ByteBuffer.allocate(4)
                .putInt(MessageType.SENSOR_DESCRIPTION)
                .array();
        client.publish(advsertiseTopic, concatByteArrays(messageHeader, payload), 0, false);

        client.disconnect();
    }

    public ClientSensor<?> registerSensor(SensorBuilder.Client<?> sensorBuilder) {
        ClientSensor<?> sensor = sensorBuilder.build();
        String sensorName = sensor.getName();
        if (sensors.contains(sensor))
            throw new IllegalArgumentException("There is already a sensor with the same name");
        sensor.initialize();
        return sensor;
    }

    public void stopAll() {
        for (ClientSensor<?> sensor : sensors) {
            sensor.stopServices();
        }
    }

    private byte[] concatByteArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

}
