package fr.phyxit.id.storage;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StorageSpace<T> {

    public enum Hook {
        ON_REMOVE
    }

    private final Map<Class<?>, StorageMode<T, ?>> storageModesByKeyClass = new HashMap<>();
    private final Map<Class<?>, Map<Object, Set<T>>> elementsByKeyClass = new HashMap<>();
    private final Set<T> elements = new HashSet<>();

    private final Map<Hook, Set<Consumer<T>>> hooks = new HashMap<>();

    private final ReentrantLock mutex = new ReentrantLock(true);

    public StorageSpace(StorageMode<T, ?> ... storageModes) {
        for (StorageMode<T, ?> storageMode : storageModes) {
            storageModesByKeyClass.put(storageMode.getKeyClass(), storageMode);
        }
    }

    public void addHook(Hook hook, Consumer<T> consumer) { hooks.computeIfAbsent(hook, hook1 -> new HashSet<>()).add(consumer); }

    /**
     * <b>ATTENTION</b>
     * Notifying hooks should be done when the mutex has been unlocked to avoid
     * getting in a thread that is waiting for the mutex to be released.
     * @param hook
     * @param element
     */
    private void notifyHooks(Hook hook, T element) {
        if (!hooks.containsKey(hook)) return;
        hooks.get(hook).forEach(consumer -> consumer.accept(element));
    }

    private void lockMutex() { mutex.lock(); }
    private void unlockMutex() { mutex.unlock(); }

    public void register(T element) {
        lockMutex();
        for (StorageMode<T, ?> storageMode : storageModesByKeyClass.values()) {
            Class<?> keyClass = storageMode.getKeyClass();
            Object key = storageMode.computeKey(element);
            elementsByKeyClass.computeIfAbsent(keyClass, aClass -> new HashMap<>())
                    .computeIfAbsent(key, o -> new HashSet<>()).add(element);
        }
        elements.add(element);
        unlockMutex();
    }

    private void removeFromAllStorages(T element) {
        lockMutex();
        for (StorageMode<T, ?> storageMode : storageModesByKeyClass.values()) {
            Class<?> keyClass = storageMode.getKeyClass();
            if (!elementsByKeyClass.containsKey(keyClass)) continue;
            Object key = storageMode.computeKey(element);
            Set<T> elementsInKey = elementsByKeyClass.get(keyClass).get(key);
            elementsInKey.remove(element);
            if (elementsInKey.isEmpty()) elementsByKeyClass.get(keyClass).remove(key);
        }
        elements.remove(element);
        unlockMutex();
    }

    public boolean exists(Object instanceKey) {
        Class<?> keyClass = instanceKey.getClass();
        if (!storageModesByKeyClass.containsKey(keyClass)) return false;
        if (!elementsByKeyClass.containsKey(keyClass)) return false;
        return elementsByKeyClass.get(keyClass).containsKey(instanceKey);
    }

    public <K> Map<K, T> getUnmodifiableElementsBy(StorageMode<T, K> storageMode) {
        Class<?> keyClass = storageMode.getKeyClass();
        if (!storageModesByKeyClass.containsKey(keyClass)) return new HashMap<>();
        if (!elementsByKeyClass.containsKey(keyClass)) return new HashMap<>();
        return (Map<K, T>) Collections.unmodifiableMap(elementsByKeyClass.get(keyClass));
    }

    public Set<T> getUnmodifiableElementsSet() {
        return Collections.unmodifiableSet(elements);
    }

    public void forEach(Consumer<T> consumer) {
        lockMutex();
        elements.forEach(consumer);
        unlockMutex();
    }

    public Set<T> getInter(Object ... instanceKeys) {
        Set<Set<T>> sets = new HashSet<>();
        for (Object instanceKey : instanceKeys) {
            sets.add(get(instanceKey));
        }
        return sets.stream().reduce(new HashSet<>(), (set1, set2) -> {
            set1.addAll(set2.stream().filter(obj -> {
                return sets.stream().allMatch(set -> set.contains(obj));
            }).collect(Collectors.toSet()));
            return set1;
        });
    }

    public Set<T> get(Object instanceKey) {
        Class<?> keyClass = instanceKey.getClass();
        if (!storageModesByKeyClass.containsKey(keyClass)) return new HashSet<>();
        if (!elementsByKeyClass.containsKey(keyClass)) return new HashSet<>();
        if (!elementsByKeyClass.get(keyClass).containsKey(instanceKey)) return new HashSet<>();
        return Collections.unmodifiableSet(elementsByKeyClass.get(keyClass).get(instanceKey));
    }

    public T getUnique(Object instanceKey) {
        Class<?> keyClass = instanceKey.getClass();
        if (!storageModesByKeyClass.containsKey(keyClass)) return null;
        if (!elementsByKeyClass.containsKey(keyClass)) return null;
        if (!elementsByKeyClass.get(keyClass).containsKey(instanceKey)) return null;
        return elementsByKeyClass.get(keyClass).get(instanceKey).stream().findFirst().get();
    }

    public T removeByKey(Object instanceKey) {
        lockMutex();
        T element = getUnique(instanceKey);
        if (element == null) return null;
        removeFromAllStorages(element);
        unlockMutex();
        // Notifying hooks should be done here when the mutex has been unlocked to avoid
        // getting in a thread that is waiting for the mutex to be released.
        notifyHooks(Hook.ON_REMOVE, element);
        return element;
    }

    public void remove(T element) {
        if (element == null) return;
        lockMutex();
        removeFromAllStorages(element);
        unlockMutex();
        // Notifying hooks should be done here when the mutex has been unlocked to avoid
        // getting in a thread that is waiting for the mutex to be released.
        notifyHooks(Hook.ON_REMOVE, element);
    }

}


