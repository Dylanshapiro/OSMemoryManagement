package view;

import model.process.Process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

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

            return (init < this.val && init != 0) ?
                    "< 1 " + this.symbol : init / this.val + " " + this.symbol;
        }
    }

    private HashMap<String, Unit> units;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm::ss");

    private final String name;
    private final Integer id;
    private final String startTime;
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
        this.startTime = sdf.format(new Date(startTime));
        this.base = curUnit.convertPretty(base);
        this.size = curUnit.convertPretty(size);
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getBase() {
        return this.base;
    }

    public String getSize() {
        return this.size;
    }

}
