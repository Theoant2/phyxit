package sets;

import models.ServerSensor;
import sets.operations.parent.ASensorSetOperation;

import java.util.Collections;
import java.util.Set;

public class SensorSet<T> {

    private Set<ServerSensor<T>> sensors;

    public SensorSet(Set<ServerSensor<T>> sensors) {
        this.sensors = sensors;
    }

    public Set<ServerSensor<T>> getSensors() {
        return Collections.unmodifiableSet(sensors);
    }

    public <R> R performOperation(ASensorSetOperation<T, R> operation) {
        return operation.perform(this);
    }

}
