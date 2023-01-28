package sets.operations.parent;

import models.Sensor;
import sets.SensorSet;
import sets.operations.CollectionOperation;
import sets.operations.AverageOperation;
import sets.operations.ReduceOperation;

public abstract class ASensorSetOperation<T, R> {

    protected Class<T> targetClassType;

    public ASensorSetOperation(Class<T> targetClassType) {
        this.targetClassType = targetClassType;
    }

    final boolean canBeAppliedOn(Sensor<?> sensor) {
        Class<?> sensorDataClass = sensor.getSensorDataType().getDataClass();
        return sensorDataClass.equals(targetClassType);
    }

    public abstract R perform(SensorSet<T> sensors);


    public static ReduceOperation<Double> sumDouble() { return new ReduceOperation<>(Double.class, Double::sum); }
    public static ReduceOperation<Float> sumFloat() { return new ReduceOperation<>(Float.class, Float::sum); }
    public static ReduceOperation<Integer> sumInteger() { return new ReduceOperation<>(Integer.class, Integer::sum); }
    public static ReduceOperation<Long> sumLong() { return new ReduceOperation<>(Long.class, Long::sum); }

    public static AverageOperation<Double> meanDouble() { return new AverageOperation<>(Double.class, sumDouble(), (sum, count) -> sum / count); }
    public static AverageOperation<Float> meanFloat() { return new AverageOperation<>(Float.class, sumFloat(), (sum, count) -> sum / count); }
    public static AverageOperation<Integer> meanInteger() { return new AverageOperation<>(Integer.class, sumInteger(), (sum, count) -> sum / count); }
    public static AverageOperation<Long> meanLong() { return new AverageOperation<>(Long.class, sumLong(), (sum, count) -> sum / count); }

    public static CollectionOperation<Double> maxDouble() { return new CollectionOperation<>(Double.class, values -> values.stream().max(Double::compare).get()); }
    public static CollectionOperation<Float> maxFloat() { return new CollectionOperation<>(Float.class, values -> values.stream().max(Float::compare).get()); }
    public static CollectionOperation<Integer> maxInteger() { return new CollectionOperation<>(Integer.class, values -> values.stream().max(Integer::compare).get()); }
    public static CollectionOperation<Long> maxLong() { return new CollectionOperation<>(Long.class, values -> values.stream().max(Long::compare).get()); }

    public static CollectionOperation<Double> minDouble() { return new CollectionOperation<>(Double.class, values -> values.stream().min(Double::compare).get()); }
    public static CollectionOperation<Float> minFloat() { return new CollectionOperation<>(Float.class, values -> values.stream().min(Float::compare).get()); }
    public static CollectionOperation<Integer> minInteger() { return new CollectionOperation<>(Integer.class, values -> values.stream().min(Integer::compare).get()); }
    public static CollectionOperation<Long> minLong() { return new CollectionOperation<>(Long.class, values -> values.stream().min(Long::compare).get()); }

}
