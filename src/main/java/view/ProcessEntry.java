package view;

import javafx.beans.property.SimpleStringProperty;
import oshi.util.FormatUtil;

import java.util.HashMap;
import java.util.stream.Stream;

import model.process.Process;

public class ProcessEntry {

    private enum Unit {
        Byte(1, "byte", "B"),
        Megabyte(1024L * 1024L, "megabyte", "MB"),
        Gigabyte(1024L * 1024L * 1024L, "gigabyte", "GB");

        public long val;
        public String name;
        public String symbol;

        Unit(long val, String name, String symbol) {
            this.val = val;
            this.name = name;
            this.symbol = symbol;
        }

        public long convert(long init) {
            return init / this.val;
        }

        public String convertPretty(long init) {
            return init / this.val + " " + this.symbol;
        }
    }

    private HashMap<String, Unit> units;

    private final String name;
    private final Integer id;
    private final Long startTime;
    private final String base;
    private final String size;

    public ProcessEntry(Process p, String unit) {
        this(p.getName(), p.getProcId(), p.getStartTime(),
                p.getBaseAddress().get(), p.getSize(),
                unit);
    }

    public ProcessEntry(String name, int id, long startTime,
                        long base, long size, String unit) {

        Unit curUnit = Stream.of(Unit.values())
                .filter(unitEnum -> unitEnum.name.equals(unit.toLowerCase()))
                .findFirst()
                .orElse(Unit.Megabyte);

        this.name = name;
        this.id = id;
        this.startTime = startTime;

        this.base = curUnit.convertPretty(base);
        this.size = curUnit.convertPretty(size);
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

    public String getBase() {
        return this.base;
    }

    public String getSize() {
        return this.size;
    }

}
