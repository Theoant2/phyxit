package fr.phyxit.id.sets.operations;

import fr.phyxit.id.sets.SensorSet;
import fr.phyxit.id.sets.operations.parent.ASensorSetOperation;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ReduceOperation<T> extends ASensorSetOperation<T, T> {

    private BiFunction<T, T, T> reduceMethod;
    private Supplier<T> defaultValueSupplier;

    public ReduceOperation(Class<T> targetClassType, BiFunction<T, T, T> reduceMethod, Supplier<T> defaultValueSupplier) {
        super(targetClassType);
        this.reduceMethod = reduceMethod;
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    public T perform(SensorSet<T> sensors) {
        return sensors.getSensors()
                .stream()
                .map(s -> s.getProvider().getLastReceivedData())
                .reduce((v1, v2) -> reduceMethod.apply(v1, v2))
                .orElseGet(defaultValueSupplier);
    }

}
