package config;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public final class Config {

    private static final String userCfgPath = "cfg/settings.obj";

    private static Map<String, List<String>> settings;

    private static Map<String, List<String>> initDefaults() {
        Map<String, List<String>> defaults = new HashMap<>(5);
        defaults.put("delay", Arrays.asList("500"));
        defaults.put("delaySpread", Arrays.asList("500"));
        defaults.put("sizeSpread", Arrays.asList("5000"));
        defaults.put("variance", Arrays.asList("high"));
        defaults.put("nodes",new ArrayList<>(4));
        defaults.put("defaultAlgo", Arrays.asList("FirstFitAlgo"));
        return defaults;
    }

    public static void initSettings() throws IOException {

        final Function<Map<String, List<String>>, Void> createIfBad =
                (defaults) -> {
                    setupCfgDir();                  // create cfg directory in root if needed
                    writeConfigFile(defaults);  // write default config file to dir
                    settings = defaults;       // store cfg vals in this obj
                    printCfg();                     // print cfg map
                    return null;
                };

        if (!checkConfigFileExists()) {

            createIfBad.apply(initDefaults());
        } else {

            try {
                settings = readSavedConfig();
                printCfg();
            } catch (ClassNotFoundException e) {

                createIfBad.apply(initDefaults());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, List<String>> readSavedConfig() throws IOException, ClassNotFoundException {

        FileInputStream inputStream = new FileInputStream(userCfgPath);
        ObjectInputStream in = new ObjectInputStream(inputStream);


        Map<String, List<String>> settings = (Map<String, List<String>>) in.readObject();

        return settings;
    }

    private static void printCfg() {
        int maxLen = settings.keySet().stream()
                .max(Comparator.comparingInt(String::length))
                .get().length(); // longest key string

        settings.forEach((key, valList) -> {
            System.out.printf("%-" + maxLen + "s: %s%n",
                    key, valList);
        });
    }

    private static void writeConfigFile(Map<String, List<String>> defaults) {
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

    private static void setupCfgDir() {
        String cfgDirPath = userCfgPath.substring(0, userCfgPath.lastIndexOf('/'));
        File cfgDir = new File(cfgDirPath);

        if (!cfgDir.exists() || !cfgDir.isDirectory()) {
            cfgDir.mkdirs();
        }
    }

    private static boolean checkConfigFileExists() {
        return new File(userCfgPath).isFile();
    }

    private static void trySetSetting(String target, String newSetting) {
        settings.replace(target, Arrays.asList(newSetting));
        writeConfigFile(settings);
    }

    private static void trySetSetting(String target, List<String> newSetting) {
        settings.replace(target, newSetting);
        writeConfigFile(settings);
    }

    private static Optional<List<String>> tryGetSetting(String setting) {
        return Optional.ofNullable(settings.get(setting));
    }

    // Config stuff //
    // set the spawn rate for sim source
    public static void setDelay(int delay) {
        trySetSetting("delay",
                Integer.toString(delay));
    }

    public static int getDelay() {
        return Integer.parseInt(tryGetSetting("delay")
                .get().get(0));
    }

    // set the spawn rate for sim source
    public static void setDelaySpread(int delayS) {
        trySetSetting("delaySpread",
                Integer.toString(delayS));
    }

    public static int getDelaySpread() {
        return Integer.parseInt(tryGetSetting("delaySpread")
                .get().get(0));
    }

    public static void setSizeSpread(int sizeSpread) {
        trySetSetting("sizeSpread",
                Integer.toString(sizeSpread));
    }

    public static int getSizeSpread() {
        return Integer.parseInt(tryGetSetting("sizeSpread")
                .get().get(0));
    }

    public static void setVariance(int variance) {
        trySetSetting("variance",
                Integer.toString(variance));
    }

    public static int getVariance() {
        return Integer.parseInt(tryGetSetting("variance")
                .get().get(0));
    }

    /**
     * returns false if not valid ip's
     *
     * @return boolean
     */
    public static boolean setNodes(List<String> nodes) {

        boolean allValid = nodes.stream()
                .allMatch(ipString -> {
                    return validateIpV4(ipString);
                });

        if (allValid) {
            trySetSetting("nodes", nodes);
            return true;
        } else {
            return false;
        }
    }

    private static boolean validateIpV4(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }

    public static List<String> getRemoteNodes() {
        return tryGetSetting("nodes").get();
    }

}
