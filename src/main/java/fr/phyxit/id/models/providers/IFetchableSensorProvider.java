package fr.phyxit.id.models.providers;

public interface IFetchableSensorProvider<T> extends ISensorProvider<T> {

    public T fetchData();

}
