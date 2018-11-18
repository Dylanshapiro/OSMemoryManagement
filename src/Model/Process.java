package Model;

import java.util.ArrayList;
import java.util.Optional;

public class Process{

    int procId;
    Long startTime;
    ProcState state;
    Long size;
    Optional<Long> baseAddress;



    /**
     * this gets the size of the process
     * @return Long
     */
    public Long getSize() {
        return size;
    }

    /**
     *  this sets the size of the process
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * gets the base address of this process
     * base address is the start point of the process
     * caution: base address can be null
     * @return Optional</long>
     */
    public Optional<Long> getBaseAddress() {
        return baseAddress;
    }


    /**
     *  sets the base address
     *  caution: base address can be null
     * @param baseAddress
     */
    public void setBaseAddress(Optional<Long> baseAddress) {
        this.baseAddress = baseAddress;
    }

    /**
     * retunrs the start time for this process
     * @return long
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * ets the start time for the process
     * @param startTime
     */
    public void setStartTime(long startTime) {
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
