package fr.phyxit.id;

import fr.phyxit.id.models.ISensorDataType;
import fr.phyxit.id.models.SensorBuilder;
import fr.phyxit.id.models.SensorLocation;
import fr.phyxit.id.models.ServerSensor;
import fr.phyxit.id.models.frequency.ASensorFrequency;
import fr.phyxit.id.models.providers.json.CatFactJsonClientSensorProvider;
import fr.phyxit.id.restapi.http.HTTPRestServer;

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

        HTTPRestServer httpRestServer = sensorManager.createRestAPIServer();
        httpRestServer.start();

        sensorManager.enableMQTTSensorDiscovery(MASTER_TOPIC);

    }

    public static SensorManager getSensorManager() { return sensorManager; }

}
