package fr.phyxit.id.models.providers;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.nio.ByteBuffer;

public abstract class APrimitiveMQTTSensorProvider<T> extends AMQTTClientSensorProvider<T> {

    private T data;

    public APrimitiveMQTTSensorProvider(java.lang.String topic) { super(topic); }

    @Override
    protected MqttConnectOptions initializeMqttClient() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setConnectionTimeout(10);
        return options;
    }

    public abstract T parse(byte[] payload);

    @Override
    public T getLastReceivedData() { return data; }

    @Override
    public void parseMessage(byte[] payload) {
        data = parse(payload);
    }

    @Override
    public void connectionLost(Throwable cause) {}

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    public static final APrimitiveMQTTSensorProvider<?> from(java.lang.String topic, Class<?> clazz) {
        if (clazz.equals(java.lang.Double.class)) return new APrimitiveMQTTSensorProvider.Double(topic);
        if (clazz.equals(java.lang.Float.class)) return new APrimitiveMQTTSensorProvider.Float(topic);
        if (clazz.equals(java.lang.Integer.class)) return new APrimitiveMQTTSensorProvider.Integer(topic);
        if (clazz.equals(java.lang.Long.class)) return new APrimitiveMQTTSensorProvider.Long(topic);
        if (clazz.equals(java.lang.String.class)) return new APrimitiveMQTTSensorProvider.String(topic);
        if (clazz.equals(java.lang.Boolean.class)) return new APrimitiveMQTTSensorProvider.Boolean(topic);
        throw new IllegalArgumentException(java.lang.String.format("No primitive provider found for type '%s'", clazz.getName()));
    }

    public static class Double extends APrimitiveMQTTSensorProvider<java.lang.Double> {
        public Double(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.Double parse(byte[] payload) { return ByteBuffer.wrap(payload).getDouble(); }
    }

    public static class Float extends APrimitiveMQTTSensorProvider<java.lang.Float> {
        public Float(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.Float parse(byte[] payload) { return ByteBuffer.wrap(payload).getFloat(); }
    }

    public static class Integer extends APrimitiveMQTTSensorProvider<java.lang.Integer> {
        public Integer(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.Integer parse(byte[] payload) { return ByteBuffer.wrap(payload).getInt(); }
    }

    public static class Long extends APrimitiveMQTTSensorProvider<java.lang.Long> {
        public Long(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.Long parse(byte[] payload) { return ByteBuffer.wrap(payload).getLong(); }
    }

    public static class String extends APrimitiveMQTTSensorProvider<java.lang.String> {
        public String(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.String parse(byte[] payload) { return new java.lang.String(payload); }
    }

    public static class Boolean extends APrimitiveMQTTSensorProvider<java.lang.Boolean> {
        public Boolean(java.lang.String topic) {
            super(topic);
        }
        @Override
        public java.lang.Boolean parse(byte[] payload) { return payload[0] > 0; }
    }

}
