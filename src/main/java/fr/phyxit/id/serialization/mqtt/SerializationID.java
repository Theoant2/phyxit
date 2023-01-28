package fr.phyxit.id.serialization.mqtt;

public interface SerializationID {

    // 100 - 199 SensorLocation
    public static int SIMPLE_SENSOR_LOCATION = 100;

    // 200 - 299 ISensorDataType
    public static int SENSOR_DATA_TYPE_BOOLEAN = 200;
    public static int SENSOR_DATA_TYPE_DOUBLE = 201;
    public static int SENSOR_DATA_TYPE_FLOAT = 202;
    public static int SENSOR_DATA_TYPE_INTEGER = 203;
    public static int SENSOR_DATA_TYPE_LONG = 204;
    public static int SENSOR_DATA_TYPE_STRING = 205;

    // 300 - 399 ASensorFrequency
    public static int SIMPLE_SENSOR_FREQUENCY = 300;

}
