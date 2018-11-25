package model;

import java.util.List;

public interface ProcessSource {

    public Process getProcess(int i);

    public List<Process> getAll();
    
    public Process generateProcess();
    
}
