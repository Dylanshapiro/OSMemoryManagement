package model;

import java.util.Optional;

public class Process {

    private int procId;
    private long startTime;
    private ProcState state;
    private Long size;
    private String name;
    Optional<Long> baseAddress;

    /**
     * constructor
     *
     * @param procId
     * @param startTime
     * @param size
     */
    public Process(String name, int procId, long startTime, Long size) {
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
    public Long getSize() {
        return size;
    }

    /**
     * this sets the size of the process
     *
     * @param size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * gets the base address of this process
     * base address is the start point of the process
     * caution: base address can be null
     *
     * @return Optional<Integer>
     */
    public Optional<Long> getBaseAddress() {
        return baseAddress;
    }

    /**
     * sets the base address
     * caution: base address can be null
     *
     * @param baseAddress
     */
    public void setBaseAddress(Long baseAddress) {
        this.baseAddress = Optional.of(baseAddress);
    }

    /**

     * returns the start time for this process
     *
     * @return long
     */
    public long getStartTime() {
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


    public String toString() {
        return (name + "\t\t" + procId + "\t\t" + size + "\t\t" + getBaseAddress().get());
    }
}

