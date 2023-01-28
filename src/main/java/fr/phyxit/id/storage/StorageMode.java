package fr.phyxit.id.storage;

import java.util.UUID;

public interface StorageMode<T, K> {

    Class<K> getKeyClass();
    K computeKey(T element);

}