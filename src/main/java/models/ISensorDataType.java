package models;

import models.providers.APrimitiveMQTTSensorProvider;
import models.providers.ISensorProvider;

public interface ISensorDataType<T> {

    public static final ISensorDataType<String> REST_API_STRING = new ISensorDataType<String>() {
        @Override
        public Class<String> getDataClass() {
            return String.class;
        }

        @Override
        public ISensorProvider<String> createSensorProvider(String topic) {
            return null;
        }
    };

    public static final ISensorDataType<Boolean> MQTT_BOOLEAN = new ISensorDataType<Boolean>() {
        @Override
        public Class<Boolean> getDataClass() {
            return Boolean.class;
        }

        @Override
        public ISensorProvider<Boolean> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.Boolean(topic);
        }
    };

    public static final ISensorDataType<Double> MQTT_DOUBLE = new ISensorDataType<Double>() {
        @Override
        public Class<Double> getDataClass() {
            return Double.class;
        }

        @Override
        public ISensorProvider<Double> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.Double(topic);
        }
    };

    public static final ISensorDataType<Float> MQTT_FLOAT = new ISensorDataType<Float>() {
        @Override
        public Class<Float> getDataClass() {
            return Float.class;
        }

        @Override
        public ISensorProvider<Float> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.Float(topic);
        }
    };

    public static final ISensorDataType<Integer> MQTT_INTEGER = new ISensorDataType<Integer>() {
        @Override
        public Class<Integer> getDataClass() {
            return Integer.class;
        }

        @Override
        public ISensorProvider<Integer> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.Integer(topic);
        }
    };

    public static final ISensorDataType<Long> MQTT_LONG = new ISensorDataType<Long>() {
        @Override
        public Class<Long> getDataClass() {
            return Long.class;
        }

        @Override
        public ISensorProvider<Long> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.Long(topic);
        }
    };

    public static final ISensorDataType<String> MQTT_STRING = new ISensorDataType<String>() {
        @Override
        public Class<String> getDataClass() {
            return String.class;
        }

        @Override
        public ISensorProvider<String> createSensorProvider(String topic) {
            return new APrimitiveMQTTSensorProvider.String(topic);
        }
    };

    public Class<T> getDataClass();

    public ISensorProvider<T> createSensorProvider(String topic);

}
