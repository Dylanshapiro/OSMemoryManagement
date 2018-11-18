package Model;

import java.util.ArrayList;
import java.util.Optional;

public class Process{

    int procId;
    int startTime;
    ProcState state;
    int size;
    Optional<Integer> baseAddress;

    /**
     * constructor
     * @param procId
     * @param startTime
     * @param size
     */
    public Process(int procId, int startTime, int size) {
        this.procId = procId;
        this.startTime = startTime;
        this.state = state.READY;
        this.size = size;
        this.baseAddress = null;
    }


    /**
     * this gets the size of the process
     * @return Long
     */
    public int getSize() {
        return size;
    }

    /**
     *  this sets the size of the process
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * gets the base address of this process
     * base address is the start point of the process
     * caution: base address can be null
     * @return Optional</long>
     */
    public Optional<Integer> getBaseAddress() {
        return baseAddress;
    }


    /**
     *  sets the base address
     *  caution: base address can be null
     * @param baseAddress
     */
    public void setBaseAddress(Optional<Integer> baseAddress) {
        this.baseAddress = baseAddress;
    }

    /**
     * retunrs the start time for this process
     * @return long
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * ets the start time for the process
     * @param startTime
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * returns process id number
     * @return int
     */
    public int getProcId() {
        return procId;
    }

    /**
     * sets the procId
     * @param procId
     */
    public void setProcId(int procId) {
        this.procId = procId;
    }

    /**
     * if address is null false if not null true
     * @return boolean
     */
    public boolean isAllocated(){
        if (this.getBaseAddress()== null)
            return false;
        else
            return true;
    }





}
