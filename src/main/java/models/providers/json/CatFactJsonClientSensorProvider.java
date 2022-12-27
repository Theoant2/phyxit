package models.providers.json;

import models.providers.ARestApiClientSensorProvider;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class CatFactJsonClientSensorProvider extends ARestApiClientSensorProvider<String> {

    private static final String CAT_FACT_URL = "https://catfact.ninja/fact";

    @Override
    public String fetchData() {
        try {
            JSONObject jsonObject = fetchJson(new URL(CAT_FACT_URL));
            return jsonObject.getString("fact");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
