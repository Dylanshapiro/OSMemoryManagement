package model.process;

public interface ProcessSourceObserver {
    public void newProcess(Process p);
    public void killProcess(Process p);
}
