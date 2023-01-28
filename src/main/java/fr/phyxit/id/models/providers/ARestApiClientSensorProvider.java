package fr.phyxit.id.models.providers;

import fr.phyxit.id.models.Sensor;
import fr.phyxit.id.models.SensorProviderType;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public abstract class ARestApiClientSensorProvider<T> implements ISensorProvider<T>, IFetchableSensorProvider<T> {

    protected JSONObject fetchJson(URL url) {
        try {
            String json = IOUtils.toString(url, Charset.forName("UTF-8"));
            return new JSONObject(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initializeConnection(Sensor<T> associatedSensor) {}

    @Override
    public void closeConnection() {}

    @Override
    public T getLastReceivedData() {
        return fetchData();
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
