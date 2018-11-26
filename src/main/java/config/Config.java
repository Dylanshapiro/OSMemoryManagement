package config;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class Config {

    private final String userCfgPath = "cfg/settings.obj";

    private Map<String, List<String>> settings;

    private static Map<String, List<String>> initDefaults() {
        Map<String, List<String>> defaults = new HashMap<>(5);
        defaults.put("delay", Arrays.asList("500"));
        defaults.put("delaySpread", Arrays.asList("500"));
        defaults.put("sizeSpread", Arrays.asList("5000"));
        defaults.put("variance", Arrays.asList("high"));
        defaults.put("nodes", Arrays.asList("000.000.000.000"));
        defaults.put("defaultAlgo", Arrays.asList("FirstFitAlgo"));
        return defaults;
    }

    public Config() throws IOException {
        initSettings();
    }

    private void initSettings() throws IOException {

        final Function<Map<String, List<String>>, Void> createIfBad =
                (defaults) -> {
                    setupCfgDir();                  // create cfg directory in root if needed
                    writeConfigFile(defaults);  // write default config file to dir
                    this.settings = defaults;       // store cfg vals in this obj
                    printCfg();                     // print cfg map
                    return null;
                };

        if (!this.checkConfigFileExists()) {

            createIfBad.apply(initDefaults());
        } else {

            try {
                this.settings = readSavedConfig();
                printCfg();
            } catch (ClassNotFoundException e) {

                createIfBad.apply(initDefaults());
            }
        }
    }

    private Map<String, List<String>> readSavedConfig() throws IOException, ClassNotFoundException {

        FileInputStream inputStream = new FileInputStream(userCfgPath);
        ObjectInputStream in = new ObjectInputStream(inputStream);

        Map<String, List<String>> settings =
                (Map<String, List<String>>) in.readObject();

        return settings;
    }

    private void printCfg() {
        int maxLen = this.settings.keySet().stream()
                .max(Comparator.comparingInt(String::length))
                .get().length(); // longest key string

        this.settings.forEach((key, valList) -> {
            System.out.printf("%-" + maxLen + "s: %s%n",
                    key, valList);
        });
    }

    private void writeConfigFile(Map<String, List<String>> defaults) {
        try {
            // create streams
            FileOutputStream outStream =
                    new FileOutputStream(userCfgPath);
            ObjectOutputStream out = new ObjectOutputStream(outStream);

            out.writeObject(defaults);  // write the HashMap to a file

            out.close();                // close streams
            outStream.close();

        } catch (IOException ioErr) {
            ioErr.printStackTrace();
        }
    }

    private void setupCfgDir() {
        String cfgDirPath = this.userCfgPath.substring(0, userCfgPath.lastIndexOf('/'));
        File cfgDir = new File(cfgDirPath);

        if (!cfgDir.exists() || !cfgDir.isDirectory()) {
            cfgDir.mkdirs();
        }
    }

    private boolean checkConfigFileExists() {
        return new File(userCfgPath).isFile();
    }

    public void trySetSetting(String target, String newSetting) {
        this.settings.replace(target, Arrays.asList(newSetting));
        writeConfigFile(this.settings);
    }

    public void trySetSetting(String target, List<String> newSetting) {
        this.settings.replace(target, newSetting);
        writeConfigFile(this.settings);
    }

    public Optional<List<String>> tryGetSetting(String setting) {
        return Optional.ofNullable(this.settings.get(setting));
    }
}
