package fr.phyxit.id.storage;

import fr.phyxit.id.models.ISensorDataType;
import fr.phyxit.id.models.SensorLocation;
import fr.phyxit.id.models.SensorProviderType;
import fr.phyxit.id.models.ServerSensor;
import fr.phyxit.id.models.frequency.ASensorFrequency;
import fr.phyxit.id.models.providers.SensorProviderProtocol;

import java.util.UUID;

public interface StorageModes {

    public static final StorageMode<ServerSensor<?>, String> NAME_KEYMODE = new StorageMode<ServerSensor<?>, String>() {
        @Override
        public Class<String> getKeyClass() {
            return String.class;
        }

        @Override
        public String computeKey(ServerSensor<?> element) {
            return element.getName();
        }
    };

    public static final StorageMode<ServerSensor<?>, UUID> UUID_KEYMODE = new StorageMode<ServerSensor<?>, UUID>() {
        @Override
        public Class<UUID> getKeyClass() {
            return UUID.class;
        }

        @Override
        public UUID computeKey(ServerSensor<?> element) {
            return element.getUUID();
        }
    };

    public static final StorageMode<ServerSensor<?>, ISensorDataType> DATA_TYPE_KEYMODE = new StorageMode<ServerSensor<?>, ISensorDataType>() {
        @Override
        public Class<ISensorDataType> getKeyClass() {
            return ISensorDataType.class;
        }

        @Override
        public ISensorDataType computeKey(ServerSensor<?> element) {
            return element.getSensorDataType();
        }
    };

    public static final StorageMode<ServerSensor<?>, Class> DATA_TYPE_CLASS_KEYMODE = new StorageMode<ServerSensor<?>, Class>() {
        @Override
        public Class<Class> getKeyClass() {
            return Class.class;
        }

        @Override
        public Class computeKey(ServerSensor<?> element) {
            return element.getSensorDataType().getDataClass();
        }
    };

    public static final StorageMode<ServerSensor<?>, SensorLocation> LOC_KEYMODE = new StorageMode<ServerSensor<?>, SensorLocation>() {
        @Override
        public Class<SensorLocation> getKeyClass() {
            return SensorLocation.class;
        }

        @Override
        public SensorLocation computeKey(ServerSensor<?> element) {
            return element.getSensorLoc();
        }
    };

    public static final StorageMode<ServerSensor<?>, ASensorFrequency> FREQUENCY_KEYMODE = new StorageMode<ServerSensor<?>, ASensorFrequency>() {
        @Override
        public Class<ASensorFrequency> getKeyClass() {
            return ASensorFrequency.class;
        }

        @Override
        public ASensorFrequency computeKey(ServerSensor<?> element) {
            return element.getFrequency();
        }
    };

    public static final StorageMode<ServerSensor<?>, SensorProviderProtocol> PROVIDER_PROTOCOL_KEYMODE = new StorageMode<ServerSensor<?>, SensorProviderProtocol>() {
        @Override
        public Class<SensorProviderProtocol> getKeyClass() {
            return SensorProviderProtocol.class;
        }

        @Override
        public SensorProviderProtocol computeKey(ServerSensor<?> element) {
            return element.getProvider().getProtocol();
        }
    };

    public static final StorageMode<ServerSensor<?>, SensorProviderType> PROVIDER_TYPE_KEYMODE = new StorageMode<ServerSensor<?>, SensorProviderType>() {
        @Override
        public Class<SensorProviderType> getKeyClass() {
            return SensorProviderType.class;
        }

        @Override
        public SensorProviderType computeKey(ServerSensor<?> element) {
            return element.getProvider().getType();
        }
    };


}
