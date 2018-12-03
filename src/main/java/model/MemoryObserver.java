package model;

public interface MemoryObserver {
    public void update(MemoryObservable obs, MemoryManager.MemoryEvent event);
    public void error(String err);
}
