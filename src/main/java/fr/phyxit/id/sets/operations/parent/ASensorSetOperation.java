package fr.phyxit.id.sets.operations.parent;

import fr.phyxit.id.models.Sensor;
import fr.phyxit.id.sets.operations.CollectionOperation;
import fr.phyxit.id.sets.SensorSet;
import fr.phyxit.id.sets.operations.AverageOperation;
import fr.phyxit.id.sets.operations.ReduceOperation;

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


    public static ReduceOperation<Double> sumDouble() { return new ReduceOperation<>(Double.class, Double::sum, () -> 0.0d); }
    public static ReduceOperation<Float> sumFloat() { return new ReduceOperation<>(Float.class, Float::sum, () -> 0.0f); }
    public static ReduceOperation<Integer> sumInteger() { return new ReduceOperation<>(Integer.class, Integer::sum, () -> 0); }
    public static ReduceOperation<Long> sumLong() { return new ReduceOperation<>(Long.class, Long::sum, () -> 0L); }
    public static <V> ReduceOperation<V> sum(Class<V> typeClass) {
        if (typeClass.equals(Double.class)) return (ReduceOperation<V>) sumDouble();
        if (typeClass.equals(Float.class)) return (ReduceOperation<V>) sumFloat();
        if (typeClass.equals(Integer.class)) return (ReduceOperation<V>) sumInteger();
        if (typeClass.equals(Long.class)) return (ReduceOperation<V>) sumLong();
        throw new IllegalArgumentException("Unknown type for this operation");
    }

    public static AverageOperation<Double> averageDouble() { return new AverageOperation<>(Double.class, sumDouble(), (sum, count) -> sum / count); }
    public static AverageOperation<Float> averageFloat() { return new AverageOperation<>(Float.class, sumFloat(), (sum, count) -> sum / count); }
    public static AverageOperation<Integer> averageInteger() { return new AverageOperation<>(Integer.class, sumInteger(), (sum, count) -> sum / count); }
    public static AverageOperation<Long> averageLong() { return new AverageOperation<>(Long.class, sumLong(), (sum, count) -> sum / count); }
    public static <V extends Number> AverageOperation<V> averageNumber(Class<V> typeClass) {
        if (typeClass.equals(Double.class)) return (AverageOperation<V>) averageDouble();
        if (typeClass.equals(Float.class)) return (AverageOperation<V>) averageFloat();
        if (typeClass.equals(Integer.class)) return (AverageOperation<V>) averageInteger();
        if (typeClass.equals(Long.class)) return (AverageOperation<V>) averageLong();
        throw new IllegalArgumentException("Unknown type for this operation");
    }
    public static <V> ASensorSetOperation<V, V> average(Class<V> typeClass) {
        if (!Number.class.isAssignableFrom(typeClass)) throw new IllegalArgumentException("Unknown type for this operation");
        Class<? extends Number> numberTypeClass = (Class<? extends Number>) typeClass;
        return (ASensorSetOperation<V, V>) averageNumber(numberTypeClass);
    }


    public static CollectionOperation<Double> maxDouble() { return new CollectionOperation<>(Double.class, values -> values.stream().max(Double::compare).orElse(0.0d)); }
    public static CollectionOperation<Float> maxFloat() { return new CollectionOperation<>(Float.class, values -> values.stream().max(Float::compare).orElse(0.0f)); }
    public static CollectionOperation<Integer> maxInteger() { return new CollectionOperation<>(Integer.class, values -> values.stream().max(Integer::compare).orElse(0)); }
    public static CollectionOperation<Long> maxLong() { return new CollectionOperation<>(Long.class, values -> values.stream().max(Long::compare).orElse(0L)); }
    public static <V> CollectionOperation<V> max(Class<V> typeClass) {
        if (typeClass.equals(Double.class)) return (CollectionOperation<V>) maxDouble();
        if (typeClass.equals(Float.class)) return (CollectionOperation<V>) maxFloat();
        if (typeClass.equals(Integer.class)) return (CollectionOperation<V>) maxInteger();
        if (typeClass.equals(Long.class)) return (CollectionOperation<V>) maxLong();
        throw new IllegalArgumentException("Unknown type for this operation");
    }

    public static CollectionOperation<Double> minDouble() { return new CollectionOperation<>(Double.class, values -> values.stream().min(Double::compare).orElse(0.0d)); }
    public static CollectionOperation<Float> minFloat() { return new CollectionOperation<>(Float.class, values -> values.stream().min(Float::compare).orElse(0.0f)); }
    public static CollectionOperation<Integer> minInteger() { return new CollectionOperation<>(Integer.class, values -> values.stream().min(Integer::compare).orElse(0)); }
    public static CollectionOperation<Long> minLong() { return new CollectionOperation<>(Long.class, values -> values.stream().min(Long::compare).orElse(0L)); }
    public static <V> CollectionOperation<V> min(Class<V> typeClass) {
        if (typeClass.equals(Double.class)) return (CollectionOperation<V>) minDouble();
        if (typeClass.equals(Float.class)) return (CollectionOperation<V>) minFloat();
        if (typeClass.equals(Integer.class)) return (CollectionOperation<V>) minInteger();
        if (typeClass.equals(Long.class)) return (CollectionOperation<V>) minLong();
        throw new IllegalArgumentException("Unknown type for this operation");
    }

}
