import models.*;
import models.frequency.ASensorFrequency;
import models.providers.APrimitiveMQTTSensorProvider;
import models.providers.json.CatFactJsonClientSensorProvider;
import models.publishers.ISensorPublisher;
import serialization.SensorSerializationManager;
import serialization.mqtt.MQTTSensorSerializationProtocol;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Phyxit {

    private static String MASTER_TOPIC = "tcp://localhost:1883";
    private static SensorManager sensorManager;

    public static void main(String[] args) {

        sensorManager = new SensorManager();

        SensorBuilder.Server<String> catFactSensor = ServerSensor.builder()
                .name("Cat Fact Sensor")
                .location(SensorLocation.simple("Cloud"))
                .frequency(ASensorFrequency.simple(TimeUnit.SECONDS, 10))
                .dataType(ISensorDataType.REST_API_STRING)
                .provider(new CatFactJsonClientSensorProvider());

        sensorManager.createSensor(catFactSensor);

        sensorManager.enableMQTTSensorDiscovery(MASTER_TOPIC);

    }

    public static SensorManager getSensorManager() { return sensorManager; }

}
