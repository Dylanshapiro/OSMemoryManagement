package view;

import javafx.beans.property.SimpleStringProperty;

public class ProcessEntry {

    private final String name;
    private final Integer id;
    private final Long startTime;
    private final Long base;
    private final Long size;

    public ProcessEntry(String name, int id, long startTime, long base, long size){
        this.name = name;
        this.id = id;
        this.startTime = startTime;
        this.base = base;
        this.size = size;
    }
    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getBase() {
        return base;
    }

    public Long getSize() {
        return size;
    }
}
