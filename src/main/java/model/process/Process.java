package model.process;

public class Process {

    private int procId;
    private long startTime;
    private ProcState state;
    private Long size;
    private String name;
    Long baseAddress;

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
        this.baseAddress = null;
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
    public Long getBaseAddress() {
        return baseAddress;
    }

    /**
     * sets the base address
     * caution: base address can be null
     *
     * @param baseAddress
     */
    public void setBaseAddress(Long baseAddress) {
        this.baseAddress = baseAddress;
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
     * Fetch thine name.
     *
     * @return
     */
    public String getName() {
        return name;
    }


    public String toString() {
        return (name + "\t\t" + procId + "\t\t" + size + "\t\t" + getBaseAddress());
    }

}

