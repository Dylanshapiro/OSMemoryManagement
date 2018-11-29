package model;

import java.util.List;

public interface ProcessSource {

    public List<Process> getAll();
    
    public Process generateProcess();
    
}
