package model;

import java.util.Observer;

public interface MemoryObserver {
    public void update(MemoryObservable obs, MemoryManager.MemoryEvent event);
}
