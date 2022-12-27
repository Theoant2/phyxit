package models;

import models.frequency.ASensorFrequency;
import models.providers.ISensorProvider;
import models.publishers.ISensorPublisher;

public abstract class SensorBuilder<T> {


    protected String name;
    protected ISensorDataType sensorDataType;
    protected SensorLocation sensorLoc;
    protected ASensorFrequency frequency;

    SensorBuilder() {}

    SensorBuilder(SensorBuilder otherSensorBuilder) {
        this.name = otherSensorBuilder.name;
        this.sensorDataType = otherSensorBuilder.sensorDataType;
        this.sensorLoc = otherSensorBuilder.sensorLoc;
        this.frequency = otherSensorBuilder.frequency;
    }

    public SensorBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    public <K> SensorBuilder<K> dataType(ISensorDataType<K> sensorDataType) {
        this.sensorDataType = sensorDataType;
        return (SensorBuilder<K>) this;
    }

    public SensorBuilder<T> location(SensorLocation sensorLoc) {
        this.sensorLoc = sensorLoc;
        return this;
    }

    public SensorBuilder<T> frequency(ASensorFrequency frequency) {
        this.frequency = frequency;
        return this;
    }

    public <K> SensorBuilder.Server<K> provider(ISensorProvider<K> provider) {
        return (Server<K>) new Server<>(this).provider(provider);
    }

    public <K> SensorBuilder.Client<K> publisher(ISensorPublisher publisher) {
        return (Client<K>) new Client<>(this).publisher(publisher);
    }

    public void preBuildCheck() {
        if (name == null) throw new IllegalStateException("name cannot be null");
        if (sensorDataType == null) throw new IllegalStateException("sensorDataType cannot be null");
        if (sensorLoc == null) throw new IllegalStateException("sensorLoc cannot be null");
        if (frequency == null) throw new IllegalStateException("frequency cannot be null");
    }

    public abstract <K extends Sensor<T>> K build();


    public static class Server<T> extends SensorBuilder<T> {

        private ISensorProvider provider;

        public Server() {}

        public Server(SensorBuilder otherSensorBuilder) {
            super(otherSensorBuilder);
        }

        public <K> SensorBuilder.Server<K> provider(ISensorProvider<K> provider) {
            this.provider = provider;
            return (SensorBuilder.Server<K>) this;
        }

        @Override
        public ServerSensor<T> build() {
            super.preBuildCheck();
            if (provider == null) throw new IllegalStateException("provider cannot be null");
            return new ServerSensor<T>(name, sensorDataType, sensorLoc, frequency, provider);
        }
    }

    public static class Client<T> extends SensorBuilder<T> {

        private ISensorPublisher publisher;

        public Client() {}

        public Client(SensorBuilder otherSensorBuilder) {
            super(otherSensorBuilder);
        }

        public <K> SensorBuilder.Client<K> publisher(ISensorPublisher publisher) {
            this.publisher = publisher;
            return (SensorBuilder.Client<K>) this;
        }

        @Override
        public ClientSensor<T> build() {
            super.preBuildCheck();
            if (publisher == null) throw new IllegalStateException("publisher cannot be null");
            return new ClientSensor<T>(name, sensorDataType, sensorLoc, frequency, publisher);
        }
    }


}
