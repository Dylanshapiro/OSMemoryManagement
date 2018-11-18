package Model;

import java.util.List;

public interface ProcessSource {

    public Process getProcess(int i);

    public List<Process> getAll();
    
    public void simProcess();
    
}
