package sets.operations;

import sets.SensorSet;
import sets.operations.parent.ASensorSetOperation;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionOperation<T> extends ASensorSetOperation<T, T> {

    private Function<Collection<T>, T> methodAppliedOnCollection;

    public CollectionOperation(Class<T> targetClassType, Function<Collection<T>, T> methodAppliedOnCollection) {
        super(targetClassType);
        this.methodAppliedOnCollection = methodAppliedOnCollection;
    }

    @Override
    public T perform(SensorSet<T> sensors) {
        Set<T> mappedValues = sensors.getSensors()
                .stream()
                .map(s -> s.getProvider().getLastReceivedData())
                .collect(Collectors.toSet());
        return methodAppliedOnCollection.apply(mappedValues);
    }

}
