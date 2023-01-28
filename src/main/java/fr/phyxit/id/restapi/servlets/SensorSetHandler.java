package fr.phyxit.id.restapi.servlets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.phyxit.id.SensorManager;
import fr.phyxit.id.sets.SensorSet;
import fr.phyxit.id.sets.operations.parent.ASensorSetOperation;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SensorSetHandler implements HttpHandler {

    private static Map<String, Class<?>> typePerName = Map.ofEntries(
            Map.entry("double", Double.class),
            Map.entry("float", Float.class),
            Map.entry("integer", Integer.class),
            Map.entry("long", Long.class)
    );

    private static Map<String, Function<Class<?>, ASensorSetOperation<?, ?>>> operationsPerName = Map.ofEntries(
            Map.entry("sum", ASensorSetOperation::sum),
            Map.entry("average", ASensorSetOperation::average),
            Map.entry("max", ASensorSetOperation::max),
            Map.entry("min", ASensorSetOperation::min)
    );

    private SensorManager sensorManager;

    public SensorSetHandler(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    protected JSONObject parseQueryGET(String query) {
        JSONObject object = new JSONObject();
        if (query == null) {
            return object;
        }
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                object.put(entry[0], entry[1]);
            } else {
                object.put(entry[0], "");
            }
        }
        return object;
    }

    protected static void send(HttpExchange httpExchange, Map<String, Object> map) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        JSONObject jsonObject = new JSONObject(map);
        String httpResponse = jsonObject.toString();

        byte[] bytes = httpResponse.getBytes();
        httpExchange.sendResponseHeaders(200, bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    protected static void send(HttpExchange httpExchange, JSONObject element) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        String httpResponse = element.toString();

        byte[] bytes = httpResponse.getBytes();
        httpExchange.sendResponseHeaders(200, bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    protected static void sendError(HttpExchange httpExchange, String errorMessage) throws IOException {
        Map<String, Object> map = new HashMap<>();
        setError(map, errorMessage);
        send(httpExchange, map);
    }

    protected static void setError(Map<String, Object> map, String message) {
        map.clear();
        map.put("valid", false);
        map.put("message", message);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String requestMethod = exchange.getRequestMethod();
        JSONObject parameters;

        switch (requestMethod) {
            case "POST": {
                return;
            }
            case "GET": {
                parameters = parseQueryGET(exchange.getRequestURI().getQuery());
                break;
            }
            default: {
                return;
            }
        }

        OutputStream outputStream = exchange.getResponseBody();
        InetSocketAddress remoteAddress = exchange.getRemoteAddress();

        if (!parameters.has("action")) {
            sendError(exchange, "Action should be specified");
            return;
        }
        if (!parameters.has("operation")) {
            sendError(exchange, "Operation should be specified");
            return;
        }
        if (!parameters.has("type")) {
            sendError(exchange, "Type should be specified");
            return;
        }

        String setType = parameters.getString("type");
        String operationName = parameters.getString("operation");
        if (!typePerName.containsKey(setType)) {
            sendError(exchange, "Invalid type");
            return;
        }
        if (!operationsPerName.containsKey(operationName)) {
            sendError(exchange, "Invalid operation");
            return;
        }

        Class<?> type = typePerName.get(setType);
        ASensorSetOperation operation = operationsPerName.get(operationName).apply(type);

        SensorSet<?> sensorSet = sensorManager.getByDataType(type);
        Object operationResult = sensorSet.performOperation(operation);

        JSONObject resultObject = new JSONObject();
        resultObject.put("valid", true);
        resultObject.put("result", operationResult);
        send(exchange, resultObject);
    }
}
