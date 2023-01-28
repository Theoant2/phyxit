package sets.operations;

import sets.SensorSet;
import sets.operations.parent.ASensorSetOperation;

import java.util.function.BiFunction;

public class AverageOperation<T extends Number> extends ASensorSetOperation<T, T> {

    private ReduceOperation<T> reduceOperation;
    private BiFunction<T, Integer, T> divisor;

    public AverageOperation(Class<T> targetClassType, ReduceOperation<T> reduceOperation, BiFunction<T, Integer, T> divisor) {
        super(targetClassType);
        this.reduceOperation = reduceOperation;
        this.divisor = divisor;
    }

    @Override
    public T perform(SensorSet<T> sensors) {
        return divisor.apply(reduceOperation.perform(sensors), sensors.getSensors().size());
    }

}
