package sets.operations;

import sets.SensorSet;
import sets.operations.parent.ASensorSetOperation;

import java.util.function.BiFunction;

public class ReduceOperation<T> extends ASensorSetOperation<T, T> {

    private BiFunction<T, T, T> reduceMethod;

    public ReduceOperation(Class<T> targetClassType, BiFunction<T, T, T> reduceMethod) {
        super(targetClassType);
        this.reduceMethod = reduceMethod;
    }

    @Override
    public T perform(SensorSet<T> sensors) {
        return sensors.getSensors().stream().map(s -> s.getProvider().getLastReceivedData()).reduce((v1, v2) -> reduceMethod.apply(v1, v2)).get();
    }

}
