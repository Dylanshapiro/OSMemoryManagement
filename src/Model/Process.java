package Model;

import java.util.ArrayList;
import java.util.Optional;

public class Process {

    private int procId;
    private int startTime;
    private ProcState state;
    private int size;
    private String name;
    Optional<Integer> baseAddress;

    /**
     * constructor
     *
     * @param procId
     * @param startTime
     * @param size
     */
    public Process(String name, int procId, int startTime, int size) {
        this.name = name;
        this.procId = procId;
        this.startTime = startTime;
        this.state = state.READY;
        this.size = size;
        this.baseAddress = Optional.empty();
    }

    /**
     * this gets the size of the process
     *
     * @return Long
     */
    public int getSize() {
        return size;
    }

    /**
     * this sets the size of the process
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * gets the base address of this process
     * base address is the start point of the process
     * caution: base address can be null
     *
     * @return Optional<Integer>
     */
    public Optional<Integer> getBaseAddress() {
        return baseAddress;
    }

    /**
     * sets the base address
     * caution: base address can be null
     *
     * @param baseAddress
     */
    public void setBaseAddress(int baseAddress) {
        this.baseAddress = Optional.of(baseAddress);
    }

    /**
     * returns the start time for this process
     *
     * @return Integer
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * ets the start time for the process
     *
     * @param startTime
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * returns process id number
     *
     * @return int
     */
    public int getProcId() {
        return procId;
    }

    /**
     * sets the procId
     *
     * @param procId
     */
    public void setProcId(int procId) {

        this.procId = procId;
    }

    /**
     * if address is null false if not null true
     *
     * @return boolean
     */
    public boolean isAllocated() {
        return this.baseAddress.isPresent();
    }

    /**
     * Fetch thine name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

}